package com.project.tableforyou.domain.reservationrefactor.service.queue;

import com.project.tableforyou.domain.reservationrefactor.cache.QueueReservationCache;
import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueReservationRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueReservationQueryService {
    private final QueueReservationRedisService queueReservationRedisService;
    private final QueueReservationCacheSyncService queueReservationCacheSyncService;

    /**
     * 현재 사용자의 대기열 순번을 계산하여 반환
     * - Redis에 예약 정보가 없으면 DB 동기화 시도
     * - 입장 인원 및 취소 인원을 고려하여 실제 대기 순번 계산
     *
     * @param userId       사용자 ID
     * @param restaurantId 음식점 ID
     * @return 대기 순번 (예약번호 - 입장 수 - 취소 수)
     */
    public int getUserWaitingPosition(Long userId, Long restaurantId) {
        // Redis에서 사용자의 예약 캐시 조회
        QueueReservationCache reservation = queueReservationRedisService.getReservation(userId, restaurantId);
        int reservationNumber;

        if (reservation == null) {      // 캐시 미존재 시, DB 동기화 시도
            reservationNumber = queueReservationCacheSyncService.syncUserReservationToCacheIfAbsent(userId, restaurantId);
        } else {
            reservationNumber = reservation.reservationNumber();
        }

        // 입장 인원 수 조회 (캐시 없으면 DB 조회 후 Redis에 저장)
        int entered = queueReservationCacheSyncService.getOrLoadEnteredCount(restaurantId);

        // 취소 인원 수 조회 (캐시 없으면 DB 조회 후 Redis에 저장)
        int canceled = queueReservationCacheSyncService.getOrLoadCanceledCount(restaurantId);

        return reservationNumber - entered - canceled;
    }
}

