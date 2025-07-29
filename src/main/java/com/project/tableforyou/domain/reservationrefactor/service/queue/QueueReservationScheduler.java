package com.project.tableforyou.domain.reservationrefactor.service.queue;

import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueReservationRedisService;
import com.project.tableforyou.domain.reservationrefactor.sse.service.EmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class QueueReservationScheduler {
    private final QueueReservationRedisService queueReservationRedisService;
    private final EmitterService emitterService;
    @Qualifier("queueNotificationExecutor")
    private final Executor queueNotificationExecutor;

    // 한 번에 입장 가능한 최대 인원 수
    private static final int MAX_ACTIVE = 40;

    /**
     * 매 1초마다 실행되어 각 레스토랑 대기열을 순회하면서 사용자에게 예약 화면 입장 가능 여부를 알림
     */
    @Scheduled(fixedDelay = 1000)
    public void notifyUsersInQueue() {
        Set<Long> restaurantIds = queueReservationRedisService.getAllQueuedRestaurantIds();

        for (Long restaurantId : restaurantIds) {
            notifyUsersForRestaurant(restaurantId);
        }
    }

    /**
     * 특정 레스토랑에 대해 대기열 사용자에게 SSE 알림 전송 및 입장 처리
     */
    private void notifyUsersForRestaurant(Long restaurantId) {
        int successCount = queueReservationRedisService.getReservationSuccessCount(restaurantId);
        int entryCount = queueReservationRedisService.getCurrentEntryCount(restaurantId);

        // 현재 입장 가능한 인원 수 계산
        int remainingCapacity = successCount + MAX_ACTIVE - entryCount;

        List<Long> queue = queueReservationRedisService.getQueueForRestaurant(restaurantId, 0, -1);
        if (queue.isEmpty()) return;

        // 입장 가능한 사용자 수 계산
        int entryLimit = Math.min(remainingCapacity, queue.size());

        List<Long> entryUsers = queue.subList(0, entryLimit);
        List<Long> waitingUsers = queue.subList(entryLimit, queue.size());

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 입장 가능한 사용자 처리
        for (int i = 0; i < entryUsers.size(); i++) {
            Long userId = entryUsers.get(i);
            int position = i + 1;

            futures.add(processEntryUser(restaurantId, userId, position, queue.size()));
        }

        // 입장 대기 사용자 처리
        for (int i = 0; i < waitingUsers.size(); i++) {
            Long userId = waitingUsers.get(i);
            int position = entryLimit + i + 1;

            futures.add(processWaitingUser(restaurantId, userId, position, queue.size()));
        }

        // 모든 비동기 작업 완료 후 큐 정리
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> cleanUpQueue(restaurantId, entryLimit));
    }

    /**
     * 입장 가능한 사용자에게 이벤트 전송 후
     * entryCount 증가 및 emitter 종료 처리
     */
    private CompletableFuture<Void> processEntryUser(Long restaurantId, Long userId, int position, int totalQueueSize) {
        return CompletableFuture.runAsync(() ->
                        sendQueueStatusEvent(restaurantId, userId, position, totalQueueSize, true), queueNotificationExecutor)
                .thenRunAsync(() -> {
                    queueReservationRedisService.incrementEntryCount(restaurantId);
                    emitterService.complete(restaurantId, userId);
                }, queueNotificationExecutor);
    }

    /**
     * 입장 대기 사용자에게 현재 상태만 알림 (대기 중)
     */
    private CompletableFuture<Void> processWaitingUser(Long restaurantId, Long userId, int position, int totalQueueSize) {
        return CompletableFuture.runAsync(() ->
                sendQueueStatusEvent(restaurantId, userId, position, totalQueueSize, false), queueNotificationExecutor);
    }

    /**
     * 사용자에게 queue-status 이벤트를 전송
     */
    private void sendQueueStatusEvent(Long restaurantId, Long userId, int position, int total, boolean canEnter) {
        Map<String, Object> data = Map.of(
                "myPosition", position,
                "totalWaiting", total,
                "canEnter", canEnter
        );

        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .name("queue-status")
                .data(data);

        emitterService.send(restaurantId, userId, event);
    }

    /**
     * granted 수만큼 큐에서 제거하고, 큐가 비었으면 해당 레스토랑 ID도 제거
     */
    private void cleanUpQueue(Long restaurantId, int granted) {
        if (granted > 0) {
            queueReservationRedisService.removeUsersFromQueue(restaurantId, granted);
        }
        if (queueReservationRedisService.isQueueEmpty(restaurantId)) {
            queueReservationRedisService.removeRestaurantFromQueueSet(restaurantId);
        }
    }
}