package com.project.tableforyou.domain.reservationrefactor.sse.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

public interface EmitterRepository {
    void save(Long restaurantId, Long userId, SseEmitter emitter);
    Optional<SseEmitter> get(Long restaurantId, Long userId);
    void delete(Long restaurantId, Long userId);
}
