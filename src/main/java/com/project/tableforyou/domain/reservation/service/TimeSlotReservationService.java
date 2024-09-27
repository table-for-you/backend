package com.project.tableforyou.domain.reservation.service;

import com.project.tableforyou.domain.reservation.dto.TimeSlotReservationResDto;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.entity.TimeSlotReservation;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.project.tableforyou.utils.redis.RedisProperties.RESERVATION_KEY_PREFIX;

@RequiredArgsConstructor
@Service
public class TimeSlotReservationService {

    private final RestaurantRepository restaurantRepository;
    private final RedisUtil redisUtil;

    private static final String TIME_SLOT = ":timeslot:";
    private static final long TIME_RESERVATION_TTL = 7*24*60*60;

    /* 시간대별 가게 예약 */
    public void saveTimeSlotReservation(String username, Long restaurantId, String date, TimeSlot timeSlot) {

        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" + timeSlot;

        if (isUserAlreadyInTimeSlot(username, restaurantId, date, timeSlot))
            throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);

        TimeSlotReservation timeSlotReservation = TimeSlotReservation.builder()
                .username(username)
                .timeSlot(timeSlot)
                .build();

        redisUtil.hashPutTimeSlot(key, timeSlotReservation);
        redisUtil.expire(key, TIME_RESERVATION_TTL);
    }

    /* 예약을 했는지 확인. */
    public boolean isUserAlreadyInTimeSlot(String username, Long restaurantId, String date, TimeSlot timeSlot) {
        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" +  timeSlot;
        return redisUtil.hashExisted(key, username);
    }

    /*
    * 특정 시간 예약 상태 확인
    * true = full
    */
    public boolean checkTimeSlotReservationFull(Long restaurantId, String date, TimeSlot timeSlot) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" +  timeSlot;
        int size = redisUtil.hashSize(key);

        return restaurant.getTotalSeats()/2 - size <= 0;
    }

    /* 특정 시간대 모든 예약자 가져오기 */
    public List<TimeSlotReservationResDto> findAllTimeSlotReservations(Long restaurantId, String date, TimeSlot timeSlot) {

        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" +  timeSlot;

        return redisUtil.getTimeSlotEntries(key);
    }

    /* 예약자 삭제 */
    public void deleteTimeSlotReservation(Long restaurantId, String username, String date, TimeSlot timeSlot) {

        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" +  timeSlot;
        TimeSlotReservation timeSlotReservation = redisUtil.hashGetTimeSlot(key, username);

        // 해당 예약이 존재하는 경우 삭제합니다.
        if (timeSlotReservation != null) {
            redisUtil.hashDel(key, username);
        } else {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
    }
}
