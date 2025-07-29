package com.project.tableforyou.domain.reservationrefactor.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(Long restaurantId, Long userId, SseEmitter emitter) {
        emitters.put(getKey(restaurantId, userId), emitter);
    }

    @Override
    public Optional<SseEmitter> get(Long restaurantId, Long userId) {
        return Optional.ofNullable(emitters.get(getKey(restaurantId, userId)));
    }

    @Override
    public void delete(Long restaurantId, Long userId) {
        emitters.remove(getKey(restaurantId, userId));
    }

    private String getKey(Long restaurantId, Long userId) {
        return restaurantId + ":" + userId;
    }
}
