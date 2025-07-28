package com.project.tableforyou.domain.reservationrefactor.sse.service;

import com.project.tableforyou.domain.reservationrefactor.sse.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    public void send(Long restaurantId, Long userId, SseEmitter.SseEventBuilder event) {
        emitterRepository.get(restaurantId, userId).ifPresent(emitter -> {
            try {
                emitter.send(event);
            } catch (Exception e) {
                emitter.completeWithError(e);
                emitterRepository.delete(restaurantId, userId);
            }
        });
    }

    public void complete(Long restaurantId, Long userId) {
        emitterRepository.get(restaurantId, userId).ifPresent(emitter -> {
            emitter.complete();
            emitterRepository.delete(restaurantId, userId);
        });
    }

}
