package com.project.tableforyou.domain.reservationrefactor.service.timeslot;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservationrefactor.redis.timeslot.TimeSlotReservationRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TimeSlotReservationQueryService {
    private final TimeSlotReservationRedisService timeSlotReservationRedisService;
    private final TimeSlotReservationCacheSyncService timeSlotReservationCacheSyncService;

    /**
     * 사용자의 예약 여부를 확인하고, Redis에 없으면 DB에서 복원
     * - Redis에 예약 정보가 없으면 DB 동기화 시도
     *
     * @param userId       사용자 ID
     * @param restaurantId 음식점 ID
     * @param date         예약 날짜
     * @param timeSlot     예약 타임슬롯
     * @return 항상 true (예외가 발생하지 않으면 예약이 존재함을 의미)
     */
    public boolean confirmOrCacheMyReservation(Long userId, Long restaurantId, LocalDate date, TimeSlot timeSlot) {
        // Redis에 사용자 예약 정보가 없을 경우, DB에서 동기화 시도
        if (!timeSlotReservationRedisService.isAlreadyReserved(userId, restaurantId, date.toString(), timeSlot)) {
            timeSlotReservationCacheSyncService./**
             * Redis에 유저 예약 정보가 없을 때 DB에서 강제로 캐싱하고 예약번호 반환
             * - 대기 순번 조회 시 사용됨
             *
             * @param userId       사용자 ID
             * @param restaurantId 음식점 ID
             * @return 해당 유저의 예약번호
             */syncUserReservationToCacheIfAbsent(userId, restaurantId, date, timeSlot);
        }

        // Redis에 예약 정보가 존재하거나, 동기화 성공 시 항상 true 반환
        return true;
    }
}
