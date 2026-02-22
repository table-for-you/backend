package com.project.tableforyou.domain.reservationrefactor.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<Long, Map<Long, SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(Long restaurantId, Long userId, SseEmitter emitter) {
        emitters.computeIfAbsent(restaurantId, id -> new ConcurrentHashMap<>())
                .put(userId, emitter);
    }

    @Override
    public Optional<SseEmitter> get(Long restaurantId, Long userId) {
        return Optional.ofNullable(
                emitters.getOrDefault(restaurantId, Map.of()).get(userId)
        );
    }

    @Override
    public void delete(Long restaurantId, Long userId) {
        Map<Long, SseEmitter> map = emitters.get(restaurantId);
        if (map != null) {
            map.remove(userId);
            if (map.isEmpty()) {
                emitters.remove(restaurantId);
            }
        }
    }

    public Set<Long> getUserIds(Long restaurantId) {
        return emitters.getOrDefault(restaurantId, Map.of()).keySet();
    }
}
