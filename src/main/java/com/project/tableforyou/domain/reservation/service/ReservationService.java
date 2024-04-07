package com.project.tableforyou.domain.reservation.service;

import com.project.tableforyou.domain.reservation.dto.ReservationRequestDto;
import com.project.tableforyou.domain.reservation.dto.ReservationResponseDto;
import com.project.tableforyou.domain.reservation.entity.Reservation;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.CustomException;
import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import com.project.tableforyou.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {      // 아래 redisTemplate부분 따로 나누기

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RedisUtil redisUtil;

    /* 예약자 추가 */
    public void save(String username, String restaurantName) {

        log.info("Creating Reservation");

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));
        Restaurant restaurant = restaurantRepository.findByName(restaurantName).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        Reservation reservation = new Reservation();
        reservation.setUsername(user.getUsername());
        reservation.setRestaurant(restaurant.getName());

        String key = redisUtil.generateRedisKey(restaurant.getName());
        int size = redisUtil.getReservationSizeFromRedis(key); // redis 사이즈를 통해 예약 번호 지정
        reservation.setBooking(size+1);

        redisUtil.saveReservationToRedis(key, reservation);
        log.info("Reservation created with username: {}", user.getUsername());
    }

    /* 예약 읽기 */
    public ReservationResponseDto findByBooking(String restaurant, String username) {

        return new ReservationResponseDto(redisUtil.getReservationFromRedis(redisUtil.generateRedisKey(restaurant), username));
    }

    /* 예약자 줄어들 때. */
    public String decreaseBooking(List<ReservationResponseDto> reservations) {

        log.info("Decreasing bookings for reservations");
        String user = null;

        for (ReservationResponseDto reservation: reservations) {
            String key = redisUtil.generateRedisKey(reservation.getRestaurant());
            Reservation storedReservation = redisUtil.getReservationFromRedis(key, reservation.getUsername());

            if (storedReservation != null) {
                // 예약 번호가 1인 경우 예약 삭제
                if (storedReservation.getBooking() == 1) {
                    user = storedReservation.getUsername();
                    redisUtil.deleteReservationFromRedis(key, storedReservation.getUsername());
                    log.info("Reservation with username {} deleted", storedReservation.getUsername());
                } else {
                    // 예약 번호가 1이 아닌 경우 예약 번호 감소
                    storedReservation.setBooking(storedReservation.getBooking() - 1);
                    redisUtil.saveReservationToRedis(key, storedReservation);
                }
            }
        }
        log.info("Bookings decreased successfully");
        return user;
    }

    /* 예약 미루기(미루기할 시 store 예약자 수에 대한 조건 + 뒤에 있던 사람들 앞으로 당기기 - decreaseBooking) */
    public void postponedGuestBooking(String restaurant, String username, ReservationRequestDto dto) {

        log.info("Postponing guest booking for reservation with username: {}", username);

        String key = redisUtil.generateRedisKey(restaurant);

        // Redis에서 예약 정보를 가져오기.
        Reservation reservation = redisUtil.getReservationFromRedis(key, username);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        // 예약 정보를 업데이트.
        reservation.update(dto.getBooking());

        // 업데이트된 예약 정보를 다시 Redis에 저장.
        redisUtil.saveReservationToRedis(key, reservation);
    }

    /* 해당 가게의 모든 예약자 가져오기 */
    public List<ReservationResponseDto> findAllReservation(String restaurant) {

        log.info("Finding all reservations by restaurant: {}", restaurant);
        String key = redisUtil.generateRedisKey(restaurant);

        // Redis에서 모든 예약 정보 가져오기
        return redisUtil.getEntries(key);
    }

    /* 예약자 삭제(중간사람 삭제 시 뒤에 있던 사람들 앞으로 당기기 - decreaseBooking) */
    public void delete(String restaurant, String username) {
        log.info("Deleting reservation with username {} from restaurant {}", username, restaurant);

        // Redis에서 해당 가게의 예약 정보를 가져옵니다.
        String key = redisUtil.generateRedisKey(restaurant);
        Reservation reservation = redisUtil.getReservationFromRedis(key, username);

        // 해당 예약이 존재하는 경우 삭제합니다.
        if (reservation != null) {
            redisUtil.deleteReservationFromRedis(key, username);
            log.info("Reservation with username {} from restaurant {} deleted from Redis", username, restaurant);
        } else {
            log.warn("Reservation with username {} from restaurant {} not found in Redis", username, restaurant);
        }
    }

    /* 예약자 List를 받기위한 메서드. */
    public List<ReservationResponseDto> getReservations(String restaurant, String username, ReservationRequestDto dto) {
        String key = redisUtil.generateRedisKey(restaurant);

        List<ReservationResponseDto> reservations = redisUtil.getEntries(key);

        // 예약 번호에 따라 필터링
        if (username == null && dto == null) {
            return reservations; // 예약 앞당기기
        } else {
            Reservation beforeReservation = redisUtil.getReservationFromRedis(key, username);
            List<ReservationResponseDto> decreaseReservation = new ArrayList<>();

            if (dto == null) { // 예약 삭제로 인한 뒷사람 앞당기기
                decreaseReservation = reservations.stream()
                        .filter(reservation -> reservation.getBooking() > beforeReservation.getBooking())
                        .collect(Collectors.toList());
            } else {
                // 예약 미루기로 인한 사이 번호 앞당기기
                if (dto.getBooking() > reservations.size() || dto.getBooking() <= beforeReservation.getBooking()) {
                    throw new CustomException(ErrorCode.INVALID_PARAMETER);
                } else {
                    decreaseReservation = reservations.stream()
                            .filter(reservation -> reservation.getBooking() > beforeReservation.getBooking()
                                    && reservation.getBooking() <= dto.getBooking())
                            .collect(Collectors.toList());
                }
            }
            return decreaseReservation;
        }
    }


}
