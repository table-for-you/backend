package com.project.tableforyou.domain.reservationrefactor.redis.queue;

import com.project.tableforyou.domain.reservationrefactor.cache.QueueReservationCache;
import com.project.tableforyou.domain.reservationrefactor.redis.RedisRepository;
import com.project.tableforyou.domain.reservationrefactor.redis.util.TimeUtil;
import com.project.tableforyou.domain.reservationrefactor.redis.util.constants.ReservationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueReservationRedisService {
    private final RedisRepository redisRepository;

    public void saveReservation(Long restaurantId, QueueReservationCache reservation) {
        String key = ReservationConstants.getQueueReservationKey(restaurantId);
        redisRepository.hashPut(key, reservation.userId(), reservation);
        redisRepository.expire(key, TimeUtil.getExpireSeconds());
    }

    public boolean isAlreadyReserved(Long userId, Long restaurantId) {
        return redisRepository.hashExisted(ReservationConstants.getQueueReservationKey(restaurantId), userId);
    }

    public QueueReservationCache getReservation(Long userId, Long restaurantId) {
        return redisRepository.hashGet(
                ReservationConstants.getQueueReservationKey(restaurantId),
                userId,
                QueueReservationCache.class
        );
    }

    public void setReservationNumberCounter(Long restaurantId, int reservationNumber) {
        String key = ReservationConstants.getQueueReservationNumberKey(restaurantId);
        redisRepository.set(key, reservationNumber);
        redisRepository.expire(key, TimeUtil.getExpireSeconds());
    }

    public int generateNextReservationNumber(Long restaurantId) {
        String key = ReservationConstants.getQueueReservationNumberKey(restaurantId);
        Long number = redisRepository.increase(key);
        redisRepository.expire(key, TimeUtil.getExpireSeconds());
        return number != null ? number.intValue() : 1;
    }

    public void markAsEntered(Long userId, Long restaurantId) {
        String key = ReservationConstants.getQueueReservationKey(restaurantId);
        String enteredKey = ReservationConstants.getQueueEnteredCountKey(restaurantId);
        redisRepository.hashDel(key, userId);
        redisRepository.increase(enteredKey);
        redisRepository.expire(enteredKey, TimeUtil.getExpireSeconds());
    }

    public void cancelReservation(Long userId, Long restaurantId) {
        String key = ReservationConstants.getQueueReservationKey(restaurantId);
        String canceledKey = ReservationConstants.getQueueCanceledCountKey(restaurantId);
        redisRepository.hashDel(key, userId);
        redisRepository.increase(canceledKey);
        redisRepository.expire(canceledKey, TimeUtil.getExpireSeconds());
    }

    public void setEnteredCount(Long restaurantId, int count) {
        String key = ReservationConstants.getQueueEnteredCountKey(restaurantId);
        redisRepository.set(key, count);
        redisRepository.expire(key, TimeUtil.getExpireSeconds());
    }

    public int getEnteredCount(Long restaurantId) {
        String key = ReservationConstants.getQueueEnteredCountKey(restaurantId);
        Integer count = redisRepository.get(key, Integer.class);
        return count == null ? 0 : count;
    }

    public void setCanceledCount(Long restaurantId, int count) {
        String key = ReservationConstants.getQueueCanceledCountKey(restaurantId);
        redisRepository.set(key, count);
        redisRepository.expire(key, TimeUtil.getExpireSeconds());
    }

    public int getCanceledCount(Long restaurantId) {
        String key = ReservationConstants.getQueueCanceledCountKey(restaurantId);
        Integer count = redisRepository.get(key, Integer.class);
        return count == null ? 0 : count;
    }
}

