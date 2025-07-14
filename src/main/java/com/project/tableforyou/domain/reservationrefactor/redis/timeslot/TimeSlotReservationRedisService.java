package com.project.tableforyou.domain.reservationrefactor.redis.timeslot;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservationrefactor.cache.TimeSlotReservationCache;
import com.project.tableforyou.domain.reservationrefactor.redis.RedisRepository;
import com.project.tableforyou.domain.reservationrefactor.redis.util.TimeUtil;
import com.project.tableforyou.domain.reservationrefactor.redis.util.constants.ReservationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeSlotReservationRedisService {
    private final RedisRepository redisRepository;

    public void saveReservation(Long restaurantId, String date, TimeSlotReservationCache reservation) {
        String key = ReservationConstants.getTimeSlotReservationKey(restaurantId, date, reservation.timeSlot());
        redisRepository.hashPut(key, reservation.userId(), reservation);
        redisRepository.expire(key, TimeUtil.getExpireSeconds());
    }

    public boolean isAlreadyReserved(Long userId, Long restaurantId, String date, TimeSlot timeSlot) {
        return redisRepository.hashExisted(
                ReservationConstants.getTimeSlotReservationKey(restaurantId, date, timeSlot),
                userId
        );
    }

    public TimeSlotReservationCache getReservation(Long userId, Long restaurantId, String date, TimeSlot timeSlot) {
        return redisRepository.hashGet(
                ReservationConstants.getTimeSlotReservationKey(restaurantId, date, timeSlot),
                userId,
                TimeSlotReservationCache.class
        );
    }

    public void setReservationNumberCounter(Long restaurantId, String date, TimeSlot timeSlot, int reservationNumber) {
        String key = ReservationConstants.getTimeSlotReservationNumberKey(restaurantId, date, timeSlot);
        redisRepository.set(key, reservationNumber);
        redisRepository.expire(key, TimeUtil.getExpireSeconds());
    }

    public int generateNextReservationNumber(Long restaurantId, String date, TimeSlot timeSlot) {
        String key = ReservationConstants.getTimeSlotReservationNumberKey(restaurantId, date, timeSlot);
        Long number = redisRepository.increase(key);
        redisRepository.expire(key, TimeUtil.getExpireSeconds());
        return number != null ? number.intValue() : 1;
    }

    public void cancelReservation(Long userId, Long restaurantId, String date, TimeSlot timeSlot) {
        String key = ReservationConstants.getTimeSlotReservationKey(restaurantId, date, timeSlot);
        redisRepository.hashDel(key, userId);
    }
}
