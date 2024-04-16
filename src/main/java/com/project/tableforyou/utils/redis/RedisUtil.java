package com.project.tableforyou.utils.redis;

import com.project.tableforyou.domain.reservation.dto.ReservationResponseDto;
import com.project.tableforyou.domain.reservation.entity.Reservation;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static String KEY_NAME = "_reservation";

    /* Redis에 예약 정보 저장하기 */
    public void saveReservationToRedis(String key, Reservation reservation) {
        redisTemplate.opsForHash().put(key, reservation.getUsername(), reservation);
    }

    /* Redis에서 특정 예약 정보 가져오기 */
    public Reservation getReservationFromRedis(String key, String username) {
        Reservation reservation = (Reservation) redisTemplate.opsForHash().get(key, username);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        return reservation;
    }

    /* Redis에서 예약 정보 삭제하기 */
    public void deleteReservationFromRedis(String key, String username) {
        redisTemplate.opsForHash().delete(key, username);
    }

    /* Redis에 Key에 해당하는 size 반환. */
    public int getReservationSizeFromRedis(String key) {

        Long size = redisTemplate.opsForHash().size(key); // Long 형식으로 반환됨
        return size != null ? size.intValue() : 0; // int 형식으로 변환하여 반환, null인 경우 0 반환
    }

    /* Redis에 해당 값 존재하는지 확인.*/
    public boolean existedReservation(String key, String username) {
        return redisTemplate.opsForHash().hasKey(key, username);
    }

    /* key 생성 */
    public String generateRedisKey(Long restaurantId) {
        return restaurantId + KEY_NAME;
    }

    /* Redis에서 모든 예약 정보 가져오기*/
    public List<ReservationResponseDto> getEntries(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        // 예약 정보를 DTO로 변환하여 반환
        return entries.values().stream()
                .map(entry -> (Reservation) entry)
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }
}
