package com.project.tableforyou.domain.reservationrefactor.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;

        if (type.isInstance(value)) {
            return type.cast(value);
        }

        throw new IllegalStateException("Cannot cast value to " + type.getName());
    }

    public <T> void hashPut(String key, Object hashKey, T object) {
        redisTemplate.opsForHash().put(key, String.valueOf(hashKey), object);
    }

    public <T> T hashGet(String key, Object hashKey, Class<T> type) {
        Object value = redisTemplate.opsForHash().get(key, String.valueOf(hashKey));
        if (value == null) return null;

        if (type.isInstance(value)) {
            return type.cast(value);
        }

        throw new IllegalStateException("Cannot cast value to " + type.getName());
    }

    public void hashDel(String key, Object hashKey) {
        redisTemplate.opsForHash().delete(key, String.valueOf(hashKey));
    }

    public void expire(String key, long seconds) {
        redisTemplate.expire(key, Duration.ofSeconds(seconds));
    }

    public boolean hashExisted(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, String.valueOf(hashKey));
    }

    public Long increase(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }
}
