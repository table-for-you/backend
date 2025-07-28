package com.project.tableforyou.domain.reservationrefactor.service.queue;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.domain.reservationrefactor.cache.QueueReservationCache;
import com.project.tableforyou.domain.reservationrefactor.entity.QueueReservation;
import com.project.tableforyou.domain.reservationrefactor.redis.queue.QueueReservationRedisService;
import com.project.tableforyou.domain.reservationrefactor.repository.QueueReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QueueReservationCommandService {
    private final QueueReservationRepository queueReservationRepository;
    private final QueueReservationRedisService queueReservationRedisService;
    private final QueueReservationCacheSyncService queueReservationCacheSyncService;

    /**
     * 대기열 예약 생성
     * - Redis 캐시 확인 후 없다면 DB 동기화 시도
     * - 예약 번호 계산 후 DB 및 Redis 저장
     *
     * @param userId       사용자 ID
     * @param username     사용자 이름
     * @param restaurantId 음식점 ID
     * @return 예약 번호
     */
    @Transactional
    public int create(Long userId, String username, Long restaurantId) {
        // Redis에 이미 예약된 사용자라면 예외 발생
        if (!queueReservationRedisService.isAlreadyReserved(userId, restaurantId)) {
            // 캐시가 없을 경우 DB에서 확인.
            queueReservationCacheSyncService.restoreUserReservationToCache(userId, restaurantId);
        } else {
            throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);
        }

        // 예약 번호 계산
        int reservationNumber = getReservationCount(restaurantId);

        QueueReservation queueReservation = QueueReservation.builder()
                .userId(userId)
                .username(username)
                .restaurantId(restaurantId)
                .reservationNumber(reservationNumber)
                .date(LocalDate.now())
                .build();

        // DB에 예약 저장
        queueReservationRepository.save(queueReservation);

        // Redis에 캐싱
        queueReservationRedisService.saveReservation(
                restaurantId,
                QueueReservationCache.of(userId, username, reservationNumber)
        );

        // 예약 성공 카운트
        queueReservationRedisService.incrementSuccessCount(restaurantId);

        return reservationNumber;
    }

    /**
     * 예약 수를 계산
     * - count가 1이라면 DB에서 동기화 실행 (레디스 값 손실 가능성)
     */
    private int getReservationCount(Long restaurantId) {
        int count = queueReservationRedisService.generateNextReservationNumber(restaurantId);
        if (count == 1) {
            count = queueReservationCacheSyncService.syncReservationsToCacheIfAbsent(restaurantId);
        }

        return count;
    }

    /**
     * 대기열 예약 취소
     * - DB의 예약을 취소 처리
     * - Redis에서 삭제 및 삭제 카운트 증가
     */
    @Transactional
    public void cancel(Long userId, Long restaurantId) {
        QueueReservation queueReservation =
                queueReservationRepository.findByUserIdAndRestaurantIdAndDateAndIsCanceledFalse(
                        userId, restaurantId, LocalDate.now()
                ).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        queueReservation.cancelReservation();
        queueReservationRedisService.cancelReservation(userId, restaurantId);
    }

    /**
     * 대기열 입장 처리
     * - DB에 입장 상태 저장
     * - Redis에서 캐시 삭제 및 입장 카운트 증가
     */
    @Transactional
    public void markAsEntered(Long userId, Long restaurantId) {
        QueueReservation queueReservation =
                queueReservationRepository.findByUserIdAndRestaurantIdAndDateAndIsCanceledFalse(
                        userId, restaurantId, LocalDate.now()
                ).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        queueReservation.enterRestaurant();
        queueReservationRedisService.markAsEntered(userId, restaurantId);
    }
}
