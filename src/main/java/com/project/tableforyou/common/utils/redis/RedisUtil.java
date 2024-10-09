package com.project.tableforyou.common.utils.redis;

import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.dto.TimeSlotReservationResDto;
import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import com.project.tableforyou.domain.reservation.entity.TimeSlotReservation;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    /* 남은 expired 시간 가져오기 */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /* Redis 만료시간 설정. */
    public void expire(String key, long seconds) {
        redisTemplate.expire(key, Duration.ofSeconds(seconds));
    }

    /* QueueReservation 저장 */
    public void hashPutQueue(String key, QueueReservation queueReservation) {
        redisTemplate.opsForHash().put(key, queueReservation.getUsername(), queueReservation);
    }

    /* QueueReservation 정보 가져오기 */
    public QueueReservation hashGetQueue(String key, String username) {
        QueueReservation queueReservation = (QueueReservation) redisTemplate.opsForHash().get(key, username);
        if (queueReservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        return queueReservation;
    }

    /* TimeSlotReservation 저장 */
    public void hashPutTimeSlot(String key, TimeSlotReservation timeSlotReservation) {
        redisTemplate.opsForHash().put(key, timeSlotReservation.getUsername(), timeSlotReservation);
    }

    /* TimeSlotReservation 정보 가져오기 */
    public TimeSlotReservation hashGetTimeSlot(String key, String username) {
        TimeSlotReservation timeSlotReservation = (TimeSlotReservation) redisTemplate.opsForHash().get(key, username);
        if (timeSlotReservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        return timeSlotReservation;
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

    /* QueueReservation 모든 예약자 가져오기. */
    public List<QueueReservationResDto> getQueueEntries(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        // 예약 정보를 DTO로 변환하여 반환
        return entries.values().stream()
                .map(entry -> (QueueReservation) entry)
                .map(QueueReservationResDto::new)
                .collect(Collectors.toList());
    }

    /* TimeSlot 모든 예약자 가져오기. */
    public List<TimeSlotReservationResDto> getTimeSlotEntries(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        return entries.values().stream()
                .map(entry -> (TimeSlotReservation) entry)
                .map(TimeSlotReservationResDto::new)
                .collect(Collectors.toList());
    }
}

