package com.project.tableforyou.domain.reservationrefactor.sse.service;

import com.project.tableforyou.domain.reservationrefactor.sse.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmitterService {
    private static final long TIMEOUT = 60 * 60 * 1000L;
    private final EmitterRepository emitterRepository;

    public SseEmitter create(Long restaurantId, Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.save(restaurantId, userId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(restaurantId, userId));
        emitter.onTimeout(() -> emitterRepository.delete(restaurantId, userId));
        emitter.onError((e) -> emitterRepository.delete(restaurantId, userId));

        return emitter;
    }

    public boolean send(Long restaurantId, Long userId, SseEmitter.SseEventBuilder event) {
        Optional<SseEmitter> emitter = emitterRepository.get(restaurantId, userId);

        if (emitter.isEmpty()) return false;

        try {
            emitter.get().send(event);
            return true;
        } catch (Exception e) {
            emitter.get().completeWithError(e);
            emitterRepository.delete(restaurantId, userId);
            return false;
        }
    }

    public void complete(Long restaurantId, Long userId) {
        emitterRepository.get(restaurantId, userId).ifPresent(emitter -> {
            emitter.complete();
            emitterRepository.delete(restaurantId, userId);
        });
    }

    public Set<Long> getConnectedUserIds(Long restaurantId) {
        return emitterRepository.getUserIds(restaurantId);
    }

}
