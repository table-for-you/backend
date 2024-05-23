package com.project.tableforyou.domain.reservation.service;

import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.tableforyou.utils.redis.RedisProperties.RESERVATION_KEY_PREFIX;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueReservationService {

    private final RedisUtil redisUtil;
    private static final String QUEUE = "queue:";

    /* 가게 번호표 예약자 추가 */
    public void saveQueueReservation(String username, Long restaurantId) {

        log.info("Creating Reservation");

        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;

        if (isUserAlreadyInQueue(username, restaurantId))    // 중복 예약 확인.
            throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);

        int size = getQueueWaitingCount(restaurantId); // redis 사이즈를 통해 예약 번호 지정

        QueueReservation queueReservation = QueueReservation.builder()
                .username(username)
                .booking(size+1)
                .build();

        redisUtil.hashPutQueue(key, queueReservation);
        log.info("Reservation created with username: {}", username);
    }

    /* 예약을 했는지 확인. */
    public boolean isUserAlreadyInQueue(String username, Long restaurantId) {
        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        return redisUtil.hashExisted(key, username);
    }

    /* 예약자 줄어들 때. */
    public String decreaseBooking(List<QueueReservationResDto> reservations, Long restaurantId) {

        log.info("Decreasing bookings for reservations");
        String username = null;

        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        for (QueueReservationResDto reservation : reservations) {
            QueueReservation storedQueueReservation = redisUtil.hashGetQueue(key, reservation.getUsername());

            // 예약 번호가 1인 경우 예약 삭제
            if (storedQueueReservation.getBooking() == 1) {
                username = storedQueueReservation.getUsername();
                redisUtil.hashDel(key, storedQueueReservation.getUsername());
                log.info("Reservation with username {} deleted", storedQueueReservation.getUsername());
            } else {
                // 예약 번호가 1이 아닌 경우 예약 번호 감소
                storedQueueReservation.updateBooking(storedQueueReservation.getBooking() - 1);
                redisUtil.hashPutQueue(key, storedQueueReservation);
            }

        }
        log.info("Bookings decreased successfully");
        return username;
    }

    /* 예약 미루기(미루기할 시 store 예약자 수에 대한 조건 + 뒤에 있던 사람들 앞으로 당기기 - decreaseBooking) */
    public void postponedGuestBooking(Long restaurantId, String username, QueueReservationReqDto ReservationDto) {

        log.info("Postponing guest booking for reservation with username: {}", username);

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

        log.info("Finding all reservations by restaurant: {}", restaurantId);
        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;

        // Redis에서 모든 예약 정보 가져오기
        return redisUtil.getQueueEntries(key);
    }

    /* 예약자 삭제(중간사람 삭제 시 뒤에 있던 사람들 앞으로 당기기 - decreaseBooking) */
    public void deleteQueueReservation(Long restaurantId, String username) {
        log.info("Deleting reservation with username {} from restaurant {}", username, restaurantId);

        // Redis에서 해당 가게의 예약 정보를 가져옵니다.
        String key = RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        QueueReservation queueReservation = redisUtil.hashGetQueue(key, username);

        // 해당 예약이 존재하는 경우 삭제합니다.
        if (queueReservation != null) {
            redisUtil.hashDel(key, username);
            log.info("Reservation with username {} from restaurant {} deleted from Redis", username, restaurantId);
        } else {
            log.warn("Reservation with username {} from restaurant {} not found in Redis", username, restaurantId);
        }
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
}
