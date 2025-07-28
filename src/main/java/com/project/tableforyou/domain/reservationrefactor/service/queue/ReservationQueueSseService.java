package com.project.tableforyou.domain.reservationrefactor.service.queue;

import com.project.tableforyou.domain.reservationrefactor.sse.service.EmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class ReservationQueueSseService {
    private final EmitterService emitterService;

    public SseEmitter createEmitter(Long restaurantId, Long userId) {
        return emitterService.create(restaurantId, userId);
    }
}
