package com.project.tableforyou.domain.reservationrefactor.service.queue;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueReservationRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueReservationQueueService {
    private final QueueReservationRedisService queueReservationRedisService;

    public void enqueue(Long userId, Long restaurantId) {
        if (queueReservationRedisService.isAlreadyReserved(userId, restaurantId)) {
            throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);
        }

        queueReservationRedisService.pushToQueue(userId, restaurantId);
        queueReservationRedisService.addRestaurantToQueueSet(restaurantId);
    }
}
