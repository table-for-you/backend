package com.project.tableforyou.domain.reservationrefactor.service.queue;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.domain.reservationrefactor.dto.QueueEvents;
import com.project.tableforyou.domain.reservationrefactor.event.ReservationEventPublisher;
import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueAdmissionRedisService;
import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueReservationRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueReservationQueueService {
    private final QueueReservationRedisService queueReservationRedisService;
    private final QueueAdmissionRedisService redisService;
    private final ReservationEventPublisher publisher;

    /**
     * 대기열 입장 처리
     */
    public void enqueue(Long userId, Long restaurantId) {
        if (queueReservationRedisService.isAlreadyReserved(userId, restaurantId)) {
            throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);
        }

        redisService.enqueue(restaurantId, userId);
        publisher.publishQueueJoined(new QueueEvents.QueueJoined(restaurantId, userId));
    }
}
