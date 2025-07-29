package com.project.tableforyou.domain.reservationrefactor.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void pushToList(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public <T> List<T> getList(String key, long start, long end, Class<T> type) {
        List<Object> objects = redisTemplate.opsForList().range(key, start, end);
        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }

        return objects.stream()
                .map(o -> convert(o, type))
                .collect(Collectors.toList());
    }

    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public void trimList(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
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

    public void addToSet(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void removeFromSet(String key, Object value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public <T> Set<T> getSetMembers(String key, Class<T> type) {
        Set<Object> set = redisTemplate.opsForSet().members(key);
        if (set == null || set.isEmpty()) {
            return Collections.emptySet();
        }

        return set.stream()
                .map(o -> convert(o, type))
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private <T> T convert(Object value, Class<T> type) {
        if (type == Long.class && value instanceof Integer) {
            return (T) Long.valueOf((Integer) value);
        }
        return type.cast(value); // 기본 케이스
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
