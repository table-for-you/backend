package com.project.tableforyou.domain.reservationrefactor.service.timeslot;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservationrefactor.cache.TimeSlotReservationCache;
import com.project.tableforyou.domain.reservationrefactor.dto.TimeSlotReservationDto;
import com.project.tableforyou.domain.reservationrefactor.entity.TimeSlotReservation;
import com.project.tableforyou.domain.reservationrefactor.redis.timeslot.TimeSlotReservationRedisService;
import com.project.tableforyou.domain.reservationrefactor.repository.TimeSlotReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TimeSlotReservationCommandService {
    private final TimeSlotReservationRepository timeSlotReservationRepository;
    private final TimeSlotReservationRedisService timeSlotReservationRedisService;
    private final TimeSlotReservationCacheSyncService timeSlotReservationCacheSyncService;

    /**
     * 시간대 예약 생성
     * - Redis 캐시 확인 후 없다면 DB 동기화 시도
     * - 예약 인원이 초과되면 예외 발생
     * - DB 및 Redis에 예약 정보 저장
     *
     * @param userId         사용자 ID
     * @param username       사용자 이름
     * @param restaurantId   음식점 ID
     * @param availableSeats 해당 타임슬롯 총 예약 가능 인원
     * @param date           예약 날짜
     * @param timeSlot       예약 타임슬롯
     */
    @Transactional
    public void create(Long userId, String username, Long restaurantId, int availableSeats, LocalDate date, TimeSlot timeSlot) {
        // Redis에 없을 경우 DB 복원 시도 (있다면 예외 발생)
        if (!timeSlotReservationRedisService.isAlreadyReserved(userId, restaurantId, date.toString(), timeSlot)) {
            timeSlotReservationCacheSyncService.restoreUserReservationToCache(userId, restaurantId, date, timeSlot);
        } else {
            throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);
        }

        // 좌석 수 검증 (카운터 증가 후 초과 여부 확인)
        validateAvailableSeats(restaurantId, date, timeSlot, availableSeats);

        TimeSlotReservation timeSlotReservation = TimeSlotReservation.builder()
                .userId(userId)
                .username(username)
                .restaurantId(restaurantId)
                .date(date)
                .timeSlot(timeSlot)
                .build();

        // DB에 저장
        timeSlotReservationRepository.save(timeSlotReservation);

        // Redis에 캐싱
        timeSlotReservationRedisService.saveReservation(
                restaurantId,
                date.toString(),
                TimeSlotReservationCache.of(userId, username, timeSlot));
    }

    /**
     * 예약 가능한 좌석 수 검증
     * - Redis 카운터를 먼저 증가시키고
     * - 카운터가 1이라면 DB에서 동기화 실행 (레디스 값 손실 가능성)
     * - 초과 시 예외 발생
     */
    private void validateAvailableSeats(Long restaurantId, LocalDate date, TimeSlot timeSlot, int availableSeats) {
        int count = timeSlotReservationRedisService.generateNextReservationNumber(restaurantId, date.toString(), timeSlot);
        if (count == 1) {
            count = timeSlotReservationCacheSyncService.syncReservationsToCacheIfAbsent(restaurantId, date, timeSlot);
        }

        if (count > availableSeats) {
            throw new CustomException(ErrorCode.NO_AVAILABLE_SEATS);
        }
    }

    /**
     * 시간대 예약 취소
     * - DB의 예약을 취소 처리
     * - Redis에서 삭제
     *
     * @param userId       사용자 ID
     * @param restaurantId 음식점 ID
     * @param request      예약 취소 요청 정보 (날짜, 타임슬롯)
     */
    @Transactional
    public void cancel(Long userId, Long restaurantId, TimeSlotReservationDto.Request request) {
        TimeSlotReservation timeSlotReservation =
                timeSlotReservationRepository.findByUserIdAndRestaurantIdAndDateAndTimeSlotAndIsCanceledFalseAndActiveFlagTrue(
                        userId, restaurantId, request.date(), request.timeSlot()
                ).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        timeSlotReservation.cancelReservation();
        timeSlotReservationRedisService.cancelReservation(
                userId, restaurantId, request.date().toString(), request.timeSlot()
        );
    }

    /**
     * 시간대 예약 입장 처리
     * - DB에 입장 상태 저장
     *
     * @param userId       사용자 ID
     * @param restaurantId 음식점 ID
     * @param request      입장 요청 정보 (날짜, 타임슬롯)
     */
    @Transactional
    public void markAsEntered(Long userId, Long restaurantId, TimeSlotReservationDto.Request request) {
        TimeSlotReservation timeSlotReservation =
                timeSlotReservationRepository.findByUserIdAndRestaurantIdAndDateAndTimeSlotAndIsCanceledFalseAndActiveFlagTrue(
                        userId, restaurantId, request.date(), request.timeSlot()
                ).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        timeSlotReservation.enterRestaurant();
    }
}
