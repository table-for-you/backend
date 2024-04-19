package com.project.tableforyou.utils.redis;

import com.project.tableforyou.domain.reservation.dto.ReservationResponseDto;
import com.project.tableforyou.domain.reservation.entity.Reservation;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /* Redis set 저장. */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /* Redis set 정보 가져오기. */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /* Redis set 저장. */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /* Redis set 존재 확인. */
    public boolean setExisted(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /* Redis 만료시간 설정. */
    public void expire(String key, long seconds) {
        redisTemplate.expire(key, Duration.ofSeconds(seconds));
    }

    /* Redis hash 저장. */
    public void hashPut(String key, Reservation reservation) {
        redisTemplate.opsForHash().put(key, reservation.getUsername(), reservation);
    }

    /* Redis hash 정보 가져오기. */
    public Reservation hashGet(String key, String username) {
        Reservation reservation = (Reservation) redisTemplate.opsForHash().get(key, username);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        return reservation;
    }

    /* Redis hash 삭제. */
    public void hashDel(String key, String username) {
        redisTemplate.opsForHash().delete(key, username);
    }

    /* Redis hash size 반환. */
    public int hashSize(String key) {

        Long size = redisTemplate.opsForHash().size(key); // Long 형식으로 반환됨
        return size != null ? size.intValue() : 0; // int 형식으로 변환하여 반환, null인 경우 0 반환
    }

    /* Redis hash 존재 확인. */
    public boolean hashExisted(String key, String username) {
        return redisTemplate.opsForHash().hasKey(key, username);
    }

    /* Redis에 저장된 모든 예약자 가져오기. */
    public List<ReservationResponseDto> getEntries(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        // 예약 정보를 DTO로 변환하여 반환
        return entries.values().stream()
                .map(entry -> (Reservation) entry)
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }
}

