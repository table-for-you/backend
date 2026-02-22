package com.project.tableforyou.domain.reservationrefactor.event;

import com.project.tableforyou.config.properties.RabbitProperties;
import com.project.tableforyou.domain.reservationrefactor.dto.QueueEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties properties;

    /**
     * 대기열 입장 메시지 전송
     */
    public void publishQueueJoined(QueueEvents.QueueJoined event) {
        rabbitTemplate.convertAndSend(
                properties.getExchange().getReservation(),
                properties.getRouting().getQueueJoined(),
                event
        );
    }

    /**
     * 예약 처리 종료 메시지 전송
     */
    public void publishAttemptFinished(QueueEvents.AttemptFinished event) {
        rabbitTemplate.convertAndSend(
                properties.getExchange().getReservation(),
                properties.getRouting().getAttemptFinished(),
                event
        );
    }

    /**
     * 예약 페이지 입장에 대해 TTL 기반 타임아웃 처리 예약 메시지 전송
     */
    public void publishTimeoutDelay(QueueEvents.AttemptTimeout event) {
        rabbitTemplate.convertAndSend(
                properties.getExchange().getTimeoutDelay(),
                properties.getRouting().getTimeoutDelay(),
                event
        );
    }
}
