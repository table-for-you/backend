package com.project.tableforyou.domain.reservationrefactor.service.timeslot;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservationrefactor.cache.TimeSlotReservationCache;
import com.project.tableforyou.domain.reservationrefactor.entity.TimeSlotReservation;
import com.project.tableforyou.domain.reservationrefactor.redis.timeslot.TimeSlotReservationRedisService;
import com.project.tableforyou.domain.reservationrefactor.repository.TimeSlotReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeSlotReservationCacheSyncService {
    private final TimeSlotReservationRepository timeSlotReservationRepository;
    private final TimeSlotReservationRedisService timeSlotReservationRedisService;

    /**
     * 예약 번호 생성 카운터를 Redis에 동기화
     *
     * @param restaurantId 음식점 ID
     * @param date         예약 날짜
     * @param timeSlot     예약 타임슬롯
     * @return Redis에 설정할 다음 예약 번호
     */
    public int syncReservationsToCacheIfAbsent(Long restaurantId, LocalDate date, TimeSlot timeSlot) {
        int count = timeSlotReservationRepository.countByRestaurantIdAndDateAndTimeSlotAndIsCanceledFalse(
                restaurantId, date, timeSlot
        ) + 1;

        // Redis 카운터가 비정상 상태일 때만 동기화
        if (count != 1) {
            timeSlotReservationRedisService.setReservationNumberCounter(restaurantId, date.toString(), timeSlot, count);
        }

        return count;
    }

    /**
     * Redis에 예약 정보가 없지만 DB에 이미 존재하는 경우,
     * DB에서 해당 유저의 예약 정보를 복원한 뒤 예외 반환
     *
     * @param userId       사용자 ID
     * @param restaurantId 음식점 ID
     * @param date         예약 날짜
     * @param timeSlot     예약 타임슬롯
     */
    public void restoreUserReservationToCache(Long userId, Long restaurantId, LocalDate date, TimeSlot timeSlot) {
        Optional<TimeSlotReservation> timeSlotReservation =
                timeSlotReservationRepository.findByUserIdAndRestaurantIdAndDateAndTimeSlotAndIsCanceledFalseAndActiveFlagTrue(
                        userId, restaurantId, date, timeSlot
                );

        if (timeSlotReservation.isPresent()) {
            TimeSlotReservation reservation = timeSlotReservation.get();

            timeSlotReservationRedisService.saveReservation(
                    restaurantId,
                    date.toString(),
                    TimeSlotReservationCache.of(reservation.getUserId(), reservation.getUsername(), timeSlot)
            );

            throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);
        }
    }

    /**
     * Redis에 유저 예약 정보가 없을 때 DB에서 강제로 캐싱
     * - 예약이 없으면 예외 발생
     * - 예약이 있으면 Redis에 저장 후 정상 종료
     *
     * @param userId       사용자 ID
     * @param restaurantId 음식점 ID
     * @param date         예약 날짜
     * @param timeSlot     예약 타임슬롯
     */
    @Transactional
    public void syncUserReservationToCacheIfAbsent(Long userId, Long restaurantId, LocalDate date, TimeSlot timeSlot) {
        TimeSlotReservation reservation =
                timeSlotReservationRepository.findByUserIdAndRestaurantIdAndDateAndTimeSlotAndIsCanceledFalseAndActiveFlagTrue(
                        userId, restaurantId, date, timeSlot
                ).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        timeSlotReservationRedisService.saveReservation(
                restaurantId,
                date.toString(),
                TimeSlotReservationCache.of(reservation.getUserId(), reservation.getUsername(), timeSlot)
        );
    }
}
