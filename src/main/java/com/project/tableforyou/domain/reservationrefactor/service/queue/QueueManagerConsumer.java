package com.project.tableforyou.domain.reservationrefactor.service.queue;

import com.project.tableforyou.domain.reservationrefactor.dto.QueueEvents;
import com.project.tableforyou.domain.reservationrefactor.event.ReservationEventPublisher;
import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueAdmissionRedisService;
import com.project.tableforyou.domain.reservationrefactor.service.attempt.ReservationEntryAttemptService;
import com.project.tableforyou.domain.reservationrefactor.sse.service.EmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class QueueManagerConsumer {
    private final QueueAdmissionRedisService redisService;
    private final ReservationEventPublisher publisher;
    private final EmitterService emitterService;
    private final ReservationEntryAttemptService reservationEntryAttemptService;

    private static final int MAX_ACTIVE = 2;
    private static final Duration TTL = Duration.ofMinutes(2);

    // 사용자 예약 페이지 입장 처리 디바운스를 위한 설정
    private final ConcurrentHashMap<Long, AtomicBoolean> drainRunning = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicBoolean> drainPending = new ConcurrentHashMap<>();
    private static final long DRAIN_DEBOUNCE_MS = 50;

    // 사용자 대기열 번호 sse 전송 디바운스를 위한 설정
    private final ConcurrentHashMap<Long, AtomicBoolean> pushRunning = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicBoolean> pushPending = new ConcurrentHashMap<>();
    private static final long PUSH_DEBOUNCE_MS = 1000;

    private final ScheduledExecutorService queueTaskScheduler =
            Executors.newSingleThreadScheduledExecutor();

    @Qualifier("queueNotificationExecutor")
    private final Executor queueNotificationExecutor;

    /**
     * 대기열 입장 이벤트 처리
     */
    @RabbitListener(queues = "${rabbitmq.queue.joined}")
    public void onQueueJoined(QueueEvents.QueueJoined event) {
        // 빈 슬롯 만큼 추가 입장
        scheduleAdmit(event.restaurantId());

        // 순번 SSE 갱신
        schedulePushPositions(event.restaurantId());
    }

    /**
     * 예약 처리 완료 이벤트 처리
     */
    @RabbitListener(queues = "${rabbitmq.queue.attempt-finished}")
    public void onAttemptFinished(QueueEvents.AttemptFinished event) {
        // 이미 처리된 경우 무시
        if (!redisService.markDoneOnce(event.attemptId())) return;

        // 입장 처리 상태 업데이트
        finishAttemptInDb(event);

        // 슬롯 확보 및 추가 입장
        redisService.releasePermit(event.restaurantId());
        scheduleAdmit(event.restaurantId());

        //  순번 SSE 갱신
        schedulePushPositions(event.restaurantId());
    }

    /**
     * 입장 처리 상태 DB 업데이트
     */
    private void finishAttemptInDb(QueueEvents.AttemptFinished event) {
        reservationEntryAttemptService.finishAttempt(
                event.attemptId(),
                event.status(),
                event.failReason()
        );
    }

    /**
     * 예약 페이지 입장 처리를 위한 디바운스 처리
     * - 짧은 시간 안에 많은 이벤트가 동시에 몰릴 수 있음
     * - 디바운스 처리하여 여러 이벤트를 하나로 처리
     */
    private void scheduleAdmit(Long restaurantId) {
        // 변화 신호. 스케줄 실행 중 이벤트가 들어오면 무시될 수 있어 기록해둠.
        drainPending.computeIfAbsent(restaurantId, k -> new AtomicBoolean(false)).set(true);

        AtomicBoolean running = drainRunning.computeIfAbsent(restaurantId, k -> new AtomicBoolean(false));
        // 이미 등록된 스케줄이 있다면 무시
        if (!running.compareAndSet(false, true)) {
            return;
        }

        queueTaskScheduler.schedule(() -> {
            CompletableFuture.runAsync(() -> {
                try {
                    // 스케줄 실행 시 pending 초기화
                    drainPending.get(restaurantId).set(false);

                    // 실제 drain 실행
                    admitFromQueue(restaurantId);

                } finally {
                    running.set(false);

                    // 실행 중 추가 이벤트가 들어와 pending=true가 됐으면 다시 예약
                    if (drainPending.get(restaurantId).get()) {
                        scheduleAdmit(restaurantId);
                    }
                }
            }, queueNotificationExecutor);
        }, DRAIN_DEBOUNCE_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * 빈 슬롯만큼 예약 페이지 입장 처리
     * - 대기열 앞번호부터 순차적으로 입장
     * - 처리 성공 후, DB에 예약 입장 대기 상태 업데이트
     * - SSE로 입장 처리 전송 후, 예약 페이지에서 예약 가능 시간을 기록
     */
    private void admitFromQueue(Long restaurantId) {
        for (int i = 0; i < MAX_ACTIVE; i++) {
            // MAX_ACTIVE(40)보다 많은 인원을 입장 시도할 시, 무시 처리
            if (!redisService.tryAcquirePermit(restaurantId, MAX_ACTIVE)) return;

            // 대기열 앞번호 처리
            Long userId = redisService.popNextUser(restaurantId);
            if (userId == null) {
                redisService.releasePermit(restaurantId);
                return;
            }

            String attemptId = UUID.randomUUID().toString();
            long ttlSec = TTL.toSeconds();
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(ttlSec);

            // attempt DB 저장(IN_PROGRESS)
            reservationEntryAttemptService.createInProgress(
                    attemptId,
                    userId,
                    restaurantId,
                    LocalDate.now(),
                    expiresAt
            );

            // 입장 가능 SSE
            emitterService.send(restaurantId, userId,
                    SseEmitter.event().name("queue-status")
                            .data(new QueueEvents.QueueStatusPayload(true, attemptId, ttlSec, 0, 0)));

            // timeout 예약 (delay 큐 2분 처리)
            publisher.publishTimeoutDelay(new QueueEvents.AttemptTimeout(attemptId, restaurantId, userId));
        }
    }

    /**
     * 사용자에게 대기열 순번 전송을 위한 디바운스 처리
     * - 짧은 시간 안에 많은 이벤트가 동시에 몰려 대기열의 번호가 변화할 수 있음
     * - 디바운스 처리하여 여러 이벤트를 하나로 처리
     */
    private void schedulePushPositions(Long restaurantId) {
        // 변화 신호. 등록된 스케줄링이 처리 중일때, 새로운 이벤트가 들어오면 해당 이벤트가 무시될 수 있기에 기록해둠.
        pushPending.computeIfAbsent(restaurantId, k -> new AtomicBoolean(false)).set(true);

        AtomicBoolean running = pushRunning.computeIfAbsent(restaurantId, k -> new AtomicBoolean(false));
        // 이미 등록된 스케줄이 있다면 무시
        if (!running.compareAndSet(false, true)) {
            return;
        }

        queueTaskScheduler.schedule(() -> {
            CompletableFuture.runAsync(() -> {
                try {
                    // 스케줄 실행 시, pending 초기화
                    pushPending.get(restaurantId).set(false);
                    pushPositions(restaurantId);
                } finally {
                    running.set(false);
                    // 실행 중 추가 이벤트가 들어와 pending=true가 됐으면
                    // 다시 디바운스 걸어서 한 번 더 예약
                    if (pushPending.get(restaurantId).get()) {
                        schedulePushPositions(restaurantId);
                    }
                }
            }, queueNotificationExecutor);
        }, PUSH_DEBOUNCE_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * 변화가 있을 때만 순번 SSE 발송
     */
    private void pushPositions(Long restaurantId) {
        Set<Long> connectedUserIds = emitterService.getConnectedUserIds(restaurantId);
        if (connectedUserIds == null || connectedUserIds.isEmpty()) return;

        long total = redisService.getQueueSize(restaurantId);

        for (Long userId : connectedUserIds) {
            Long rank = redisService.getRank(restaurantId, userId);
            if (rank == null) continue;

            long myPosition = rank + 1;
            emitterService.send(restaurantId, userId,
                    SseEmitter.event().name("queue-status")
                            .data(new QueueEvents.QueueStatusPayload(false, null, 0, myPosition, total)));
        }
    }
}
