package com.project.tableforyou.domain.reservation.service;

import com.project.tableforyou.domain.notification.service.NotificationService;
import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.common.fcm.util.FcmProperties;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.common.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.project.tableforyou.common.utils.redis.RedisProperties.RESERVATION_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class QueueReservationService {

    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final NotificationService notificationService;
    private final RedissonClient redissonClient;

    private static final String QUEUE = "queue:";
    private static final String LOCK = "lock:";
    private static final long QUEUE_RESERVATION_TTL = 10*60*60;
    private static final long WAIT_TIME = 5L;
    private static final long LEASE_TIME = 5L;

    /* 가게 번호표 예약자 추가 */
    public void saveQueueReservation(String username, Long restaurantId) {

        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        RLock lock = redissonClient.getFairLock(LOCK + key);

        try {
            boolean available = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!available) {
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_ERROR);
            }

            if (isUserAlreadyInQueue(username, restaurantId))    // 중복 예약 확인.
                throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);

            int size = getQueueWaitingCount(restaurantId); // redis 사이즈를 통해 예약 번호 지정

            QueueReservation queueReservation = QueueReservation.builder()
                    .username(username)
                    .booking(size + 1)
                    .build();

            redisUtil.hashPutQueue(key, queueReservation);
            redisUtil.expire(key, QUEUE_RESERVATION_TTL);

            String restaurantName = restaurantRepository.findRestaurantNameByRestaurantId(restaurantId);
            User foundUser = userRepository.findByUsername(username).orElseThrow(() ->
                    new CustomException(ErrorCode.USER_NOT_FOUND));

            // FCM 알림 및 알림 저장
            notificationService.createReservationNotification(
                    foundUser.getFcmToken(),
                    FcmProperties.RESERVATION_TITLE,
                    restaurantName + FcmProperties.QUEUE_RESERVATION_CONTENT,
                    restaurantId,
                    foundUser
            );
        } catch (InterruptedException e) {
            throw new CustomException(ErrorCode.THREAD_INTERRUPTED);
        } finally {
            lock.unlock();
        }
    }

    /* 예약을 했는지 확인. */
    private boolean isUserAlreadyInQueue(String username, Long restaurantId) {
        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        return redisUtil.hashExisted(key, username);
    }

    /* 예약자 줄어들 때. */
    public String decreaseBooking(List<QueueReservationResDto> reservations, Long restaurantId) {

        String username = null;

        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        for (QueueReservationResDto reservation : reservations) {
            QueueReservation storedQueueReservation = redisUtil.hashGetQueue(key, reservation.getUsername());

            // 예약 번호가 1인 경우 예약 삭제
            if (storedQueueReservation.getBooking() == 1) {
                username = storedQueueReservation.getUsername();
                redisUtil.hashDel(key, storedQueueReservation.getUsername());

                User foundUser = userRepository.findByUsername(username).orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND));
                String restaurantName = restaurantRepository.findRestaurantNameByRestaurantId(restaurantId);

                notificationService.createReservationNotification(
                        foundUser.getFcmToken(),
                        FcmProperties.RESTAURANT_ENTER_TITLE,
                        restaurantName + FcmProperties.RESTAURANT_ENTER_CONTENT,
                        restaurantId,
                        foundUser
                );
            } else {
                // 예약 번호가 1이 아닌 경우 예약 번호 감소
                storedQueueReservation.updateBooking(storedQueueReservation.getBooking() - 1);
                redisUtil.hashPutQueue(key, storedQueueReservation);

                if (storedQueueReservation.getBooking() == 5) {     // 5번이면, 예약 입장 대기.
                    User foundUser = userRepository.findByUsername(storedQueueReservation.getUsername()).orElseThrow(() ->
                            new CustomException(ErrorCode.USER_NOT_FOUND));
                    String restaurantName = restaurantRepository.findRestaurantNameByRestaurantId(restaurantId);

                    notificationService.createReservationNotification(
                            foundUser.getFcmToken(),
                            FcmProperties.RESTAURANT_WAIT_TITLE,
                            restaurantName + FcmProperties.RESTAURANT_WAIT_CONTENT,
                            restaurantId,
                            foundUser
                    );
                }
            }

        }
        return username;
    }

    /* 예약 미루기(미루기할 시 store 예약자 수에 대한 조건 + 뒤에 있던 사람들 앞으로 당기기 - decreaseBooking) */
    public void postponedGuestBooking(Long restaurantId, String username, QueueReservationReqDto ReservationDto) {

        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;

        // Redis에서 예약 정보를 가져오기.
        QueueReservation queueReservation = redisUtil.hashGetQueue(key, username);
        if (queueReservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        // 예약 정보를 업데이트.
        queueReservation.updateBooking(ReservationDto.getBooking());

        // 업데이트된 예약 정보를 다시 Redis에 저장.
        redisUtil.hashPutQueue(key, queueReservation);
    }

    /* 해당 가게의 모든 예약자 가져오기 */
    public List<QueueReservationResDto> findAllQueueReservations(Long restaurantId) {

        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;

        // Redis에서 모든 예약 정보 가져오기
        return redisUtil.getQueueEntries(key);
    }

    /* 예약자 삭제(중간사람 삭제 시 뒤에 있던 사람들 앞으로 당기기 - decreaseBooking) */
    public void deleteQueueReservation(Long restaurantId, String username) {

        // Redis에서 해당 가게의 예약 정보를 가져옵니다.
        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        QueueReservation queueReservation = redisUtil.hashGetQueue(key, username);

        // 해당 예약이 존재하는 경우 삭제합니다.
        if (queueReservation != null)
            redisUtil.hashDel(key, username);

        String restaurantName = restaurantRepository.findRestaurantNameByRestaurantId(restaurantId);
        User foundUser = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        // FCM 알림 및 알림 저장
        notificationService.createReservationNotification(
                foundUser.getFcmToken(),
                FcmProperties.CANCEL_RESERVATION_TITLE,
                restaurantName + FcmProperties.CANCEL_RESERVATION_CONTENT,
                restaurantId,
                foundUser
        );
    }

    /* 예약자 List를 받기위한 메서드. */
    public List<QueueReservationResDto> getQueueReservations(Long restaurantId, String username,
                                                        QueueReservationReqDto ReservationDto) {

        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;

        List<QueueReservationResDto> reservations = redisUtil.getQueueEntries(key);

        // 예약 번호에 따라 필터링
        if (username == null && ReservationDto == null) {
            return reservations; // 예약 앞당기기

        } else {
            QueueReservation beforeQueueReservation = redisUtil.hashGetQueue(key, username);
            List<QueueReservationResDto> decreaseReservation = new ArrayList<>();

            if (ReservationDto == null) { // 예약 삭제로 인한 뒷사람 앞당기기
                decreaseReservation = reservations.stream()
                        .filter(reservation -> reservation.getBooking() > beforeQueueReservation.getBooking())
                        .collect(Collectors.toList());

            } else {
                // 예약 미루기로 인한 사이 번호 앞당기기
                if (ReservationDto.getBooking() > reservations.size()
                        || ReservationDto.getBooking() <= beforeQueueReservation.getBooking()) {
                    throw new CustomException(ErrorCode.INVALID_PARAMETER);

                } else {
                    decreaseReservation = reservations.stream()
                            .filter(reservation -> reservation.getBooking() > beforeQueueReservation.getBooking()
                                    && reservation.getBooking() <= ReservationDto.getBooking())
                            .collect(Collectors.toList());
                }
            }
            return decreaseReservation;
        }
    }

    /* 가게 예약자 수 읽기 */
    public int getQueueWaitingCount(Long restaurantId) {

        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        return redisUtil.hashSize(key); // redis 사이즈를 통해 예약 번호 지정
    }

    /* 나의 예약 번호 불러오기 */
    public int getMyBooking(Long restaurantId, String username) {
        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        QueueReservation storedQueueReservation = redisUtil.hashGetQueue(key, username);

        return storedQueueReservation.getBooking();
    }
}
