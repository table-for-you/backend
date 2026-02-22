package com.project.tableforyou.domain.reservationrefactor.service.timeout;

import com.project.tableforyou.domain.reservationrefactor.dto.QueueEvents;
import com.project.tableforyou.domain.reservationrefactor.entity.ReservationEntryAttempt;
import com.project.tableforyou.domain.reservationrefactor.event.ReservationEventPublisher;
import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueAdmissionRedisService;
import com.project.tableforyou.domain.reservationrefactor.service.attempt.ReservationEntryAttemptService;
import com.project.tableforyou.domain.reservationrefactor.type.AttemptStatus;
import com.project.tableforyou.domain.reservationrefactor.type.FailReason;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttemptTimeoutConsumer {
    private final QueueAdmissionRedisService redisService;
    private final ReservationEntryAttemptService reservationEntryAttemptService;
    private final ReservationEventPublisher publisher;

    /**
     * 예약 페이지 입장 후, 설정된 TTL이 지난 경우 처리
     * - 타임아웃 전에 이미 처리된 경우 무시
     * - Redis에서 정보 확인
     * - 데이터 손실을 고려하여 DB에서도 2차 확인
     */
    @RabbitListener(queues = "${rabbitmq.queue.timeout}")
    public void onTimeout(QueueEvents.AttemptTimeout event) {
        String attemptId = event.attemptId();

        // 타임아웃 전에 처리된 경우 (레디스에서 1차 확인)
        if (redisService.isDone(attemptId)) {
            return;
        }

        // 타임아웃 전에 처리된 경우 확인 (DB 상태 확인)
        ReservationEntryAttempt attempt = reservationEntryAttemptService.readByAttemptId(attemptId).orElse(null);
        if (attempt == null) {
            return;
        }

        // 이미 종료되었다면 무시
        if (attempt.isFinished()) {
            return;
        }

        // 타임아웃 이벤트 발행
        publisher.publishAttemptFinished(new QueueEvents.AttemptFinished(
                event.attemptId(),
                event.restaurantId(),
                event.userId(),
                AttemptStatus.TIMEOUT,
                FailReason.EXPIRED
        ));
    }
}
