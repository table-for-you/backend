package com.project.tableforyou.domain.reservationrefactor.service.queue;

import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueReservationRedisService;
import com.project.tableforyou.domain.reservationrefactor.sse.service.EmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class QueueReservationScheduler {
    private final QueueReservationRedisService queueReservationRedisService;
    private final EmitterService emitterService;

    // 한 번에 입장 가능한 최대 인원 수
    private static final int MAX_ACTIVE = 40;

    /**
     * 매 1초마다 실행되어 각 레스토랑 대기열을 순회하면서 사용자에게 예약 화면 입장 가능 여부를 알림
     */
    @Scheduled(fixedDelay = 1000)
    public void notifyUsersInQueue() {
        Set<Long> restaurantIds = queueReservationRedisService.getAllQueuedRestaurantIds();

        for (Long restaurantId : restaurantIds) {
            notifyUsersForRestaurant(restaurantId);
        }
    }

    /**
     * 특정 레스토랑에 대해 대기열 사용자에게 SSE 알림 전송 및 입장 처리
     */
    private void notifyUsersForRestaurant(Long restaurantId) {
        int successCount = queueReservationRedisService.getReservationSuccessCount(restaurantId);
        int entryCount = queueReservationRedisService.getCurrentEntryCount(restaurantId);

        // 현재 입장 가능한 인원 수 계산
        int remainingCapacity = successCount + MAX_ACTIVE - entryCount;

        List<Long> queue = queueReservationRedisService.getQueueForRestaurant(restaurantId, 0, -1);
        int granted = 0;

        for (int i = 0; i < queue.size(); i++) {
            Long userId = queue.get(i);
            int position = i + 1;
            boolean canEnter = (granted < remainingCapacity);

            // 사용자에게 현재 대기열 위치, 대기 인원, 입장 가능 여부 전송
            sendQueueStatus(restaurantId, userId, position, queue.size(), canEnter);

            if (canEnter) {
                // 실제 입장 처리 - entryCount 증가 + SSE 연결 종료
                queueReservationRedisService.incrementEntryCount(restaurantId);
                emitterService.complete(restaurantId, userId);
                granted++;
            }
        }

        // 큐 및 레스토랑 세트 정리
        cleanUpQueue(restaurantId, granted);
    }

    /**
     * 특정 사용자에게 queue-status 이벤트 전송
     */
    private void sendQueueStatus(Long restaurantId, Long userId, int position, int totalWaiting, boolean canEnter) {
        Map<String, Object> data = Map.of(
                "myPosition", position,
                "totalWaiting", totalWaiting,
                "canEnter", canEnter
        );

        emitterService.send(
                restaurantId,
                userId,
                SseEmitter.event()
                        .name("queue-status")
                        .data(data)
        );
    }

    /**
     * granted 수만큼 큐에서 제거하고, 큐가 비었으면 해당 레스토랑 ID도 제거
     */
    private void cleanUpQueue(Long restaurantId, int granted) {
        if (granted > 0) {
            queueReservationRedisService.removeUsersFromQueue(restaurantId, granted);
        }

        if (queueReservationRedisService.isQueueEmpty(restaurantId)) {
            queueReservationRedisService.removeRestaurantFromQueueSet(restaurantId);
        }
    }
}
