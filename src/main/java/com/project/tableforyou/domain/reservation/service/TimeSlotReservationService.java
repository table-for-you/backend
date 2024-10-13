package com.project.tableforyou.domain.reservation.service;

import com.project.tableforyou.common.fcm.util.FcmProperties;
import com.project.tableforyou.domain.notification.service.NotificationService;
import com.project.tableforyou.domain.reservation.dto.RestaurantNameSeatsDto;
import com.project.tableforyou.domain.reservation.dto.TimeSlotReservationResDto;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.entity.TimeSlotReservation;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.common.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.project.tableforyou.common.utils.redis.RedisProperties.RESERVATION_KEY_PREFIX;

@RequiredArgsConstructor
@Service
public class TimeSlotReservationService {

    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final NotificationService notificationService;
    private final RedissonClient redissonClient;

    private static final String LOCK = "lock:";
    private static final String TIME_SLOT = ":timeslot:";
    private static final long TIME_RESERVATION_TTL = 7 * 24 * 60 * 60;
    private static final long WAIT_TIME = 5L;
    private static final long LEASE_TIME = 5L;

    /* 시간대별 가게 예약 */
    public void saveTimeSlotReservation(String username, Long restaurantId, String date, TimeSlot timeSlot) {

        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" + timeSlot;
        RLock lock = redissonClient.getLock(LOCK + key);

        try {
            boolean available = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!available) {
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_ERROR);
            }

            if (isUserAlreadyInTimeSlot(username, restaurantId, date, timeSlot))
                throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);

            RestaurantNameSeatsDto result = restaurantRepository.findRestaurantNameAndSeatsByRestaurantId(restaurantId);
            String restaurantName = result.getRestaurantName();
            int totalSeats = result.getTotalSeats();

            int nowReservationSize = getReservationSizeInTimeSlot(key);

            if (totalSeats / 2 <= nowReservationSize) {
                throw new CustomException(ErrorCode.NO_AVAILABLE_SEATS);
            }

            TimeSlotReservation timeSlotReservation = TimeSlotReservation.builder()
                    .username(username)
                    .timeSlot(timeSlot)
                    .build();

            redisUtil.hashPutTimeSlot(key, timeSlotReservation);
            redisUtil.expire(key, TIME_RESERVATION_TTL);

            User foundUser = userRepository.findByUsername(username).orElseThrow(() ->
                    new CustomException(ErrorCode.USER_NOT_FOUND));

            notificationService.createReservationNotification(
                    foundUser.getFcmToken(),
                    FcmProperties.RESERVATION_TITLE,
                    restaurantName + FcmProperties.TIME_RESERVATION_CONTENT + date + "_" + timeSlot,
                    restaurantId,
                    foundUser
            );
        } catch (InterruptedException e) {
            throw new CustomException(ErrorCode.THREAD_INTERRUPTED);
        } finally {
            lock.unlock();
        }
    }

    /* 시간대 사이즈 불러오기 */
    private int getReservationSizeInTimeSlot(String key) {
        return redisUtil.hashSize(key);
    }

    /* 예약을 했는지 확인. */
    public boolean isUserAlreadyInTimeSlot(String username, Long restaurantId, String date, TimeSlot timeSlot) {
        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" + timeSlot;
        return redisUtil.hashExisted(key, username);
    }

    /*
     * 특정 시간 예약 상태 확인
     * true = full
     */
    public Map<TimeSlot, Integer> checkTimeSlotReservationFull(Long restaurantId, String date) {

        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_";

        Map<TimeSlot, Integer> timeSlotBooleanMap = new LinkedHashMap<>();
        int size;

        for (TimeSlot slot : TimeSlot.values()) {
            size = redisUtil.hashSize(key + slot);
            timeSlotBooleanMap.put(slot, size);
        }

        return timeSlotBooleanMap;
    }

    /* 특정 시간대 모든 예약자 가져오기 */
    public List<TimeSlotReservationResDto> findAllTimeSlotReservations(Long restaurantId, String date, TimeSlot timeSlot) {

        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" + timeSlot;

        return redisUtil.getTimeSlotEntries(key);
    }

    /* 예약자 삭제 */
    public void deleteTimeSlotReservation(Long restaurantId, String username, String date, TimeSlot timeSlot) {

        String key = RESERVATION_KEY_PREFIX + restaurantId + TIME_SLOT + date + "_" + timeSlot;
        TimeSlotReservation timeSlotReservation = redisUtil.hashGetTimeSlot(key, username);

        // 해당 예약이 존재하는 경우 삭제합니다.
        if (timeSlotReservation != null) {
            redisUtil.hashDel(key, username);
        } else {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        String restaurantName = restaurantRepository.findRestaurantNameByRestaurantId(restaurantId);
        User foundUser = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        notificationService.createReservationNotification(
                foundUser.getFcmToken(),
                FcmProperties.CANCEL_RESERVATION_TITLE,
                restaurantName + FcmProperties.CANCEL_RESERVATION_CONTENT,
                restaurantId,
                foundUser
        );
    }
}
