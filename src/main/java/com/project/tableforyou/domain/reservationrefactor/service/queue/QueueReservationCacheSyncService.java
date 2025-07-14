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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueReservationCacheSyncService {
    private final QueueReservationRepository queueReservationRepository;
    private final QueueReservationRedisService queueReservationRedisService;

    /**
     * 예약 번호 생성 카운터를 Redis에 동기화
     *
     * @param restaurantId 음식점 ID
     * @return 현재까지의 예약 수 + 1
     */
    public int syncReservationsToCacheIfAbsent(Long restaurantId) {
        int count = queueReservationRepository.countByRestaurantIdAndDate(
                restaurantId, LocalDate.now()
        ) + 1;

        // Redis 카운터가 비정상 상태일 때만 동기화
        if (count != 1) {
            queueReservationRedisService.setReservationNumberCounter(restaurantId, count);
        }

        return count;
    }

    /**
     * 입장 인원 수 조회 및 Redis에 동기화
     *
     * @param restaurantId 음식점 ID
     * @return 입장 완료된 인원 수
     */
    public int getOrLoadEnteredCount(Long restaurantId) {
        int count = queueReservationRedisService.getEnteredCount(restaurantId);

        if (count == 0) {       // count가 0이면, 레디스 값 손실 가능성 -> DB에서 동기화 시도
            count = queueReservationRepository.countByRestaurantIdAndDateAndIsEnteredTrue(
                    restaurantId, LocalDate.now());
            if (count != 0) {   // Redis 카운터가 비정상 상태일 때만 동기화
                queueReservationRedisService.setEnteredCount(restaurantId, count);
            }
        }

        return count;
    }

    /**
     * 취소 인원 수 조회 및 Redis에 동기화
     *
     * @param restaurantId 음식점 ID
     * @return 취소된 인원 수
     */
    public int getOrLoadCanceledCount(Long restaurantId) {
        int count = queueReservationRedisService.getCanceledCount(restaurantId);

        if (count == 0) {       // count가 0이면, 레디스 값 손실 가능성 -> DB에서 동기화 시도
            count = queueReservationRepository.countByRestaurantIdAndDateAndIsCanceledTrue(
                    restaurantId, LocalDate.now());
            if (count != 0) {   // Redis 카운터가 비정상 상태일 때만 동기화
                queueReservationRedisService.setCanceledCount(restaurantId, count);
            }
        }

        return count;
    }

    /**
     * Redis에 예약 정보가 없지만 DB에 이미 존재하는 경우,
     * DB에서 해당 유저의 예약 정보를 복원한 뒤 예외 반환
     *
     * @param userId       사용자 ID
     * @param restaurantId 음식점 ID
     */
    public void restoreUserReservationToCache(Long userId, Long restaurantId) {
        Optional<QueueReservation> queueReservation =
                queueReservationRepository.findByUserIdAndRestaurantIdAndDateAndIsCanceledFalse(
                        userId, restaurantId, LocalDate.now()
                );

        if (queueReservation.isPresent()) {
            QueueReservation reservation = queueReservation.get();

            queueReservationRedisService.saveReservation(
                    restaurantId,
                    QueueReservationCache.of(
                            reservation.getUserId(),
                            reservation.getUsername(),
                            reservation.getReservationNumber()
                    )
            );

            throw new CustomException(ErrorCode.ALREADY_USER_RESERVATION);
        }
    }

    /**
     * Redis에 유저 예약 정보가 없을 때 DB에서 강제로 캐싱하고 예약번호 반환
     *
     * @param userId       사용자 ID
     * @param restaurantId 음식점 ID
     * @return 해당 유저의 예약번호
     */
    @Transactional
    public int syncUserReservationToCacheIfAbsent(Long userId, Long restaurantId) {
        QueueReservation queueReservation =
                queueReservationRepository.findByUserIdAndRestaurantIdAndDateAndIsCanceledFalse(
                        userId, restaurantId, LocalDate.now()
                ).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        queueReservationRedisService.saveReservation(
                restaurantId,
                QueueReservationCache.of(
                        queueReservation.getUserId(),
                        queueReservation.getUsername(),
                        queueReservation.getReservationNumber()
                )
        );

        return queueReservation.getReservationNumber();
    }
}
