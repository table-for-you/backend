package com.project.tableforyou.domain.reservationrefactor.service;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.domain.reservationrefactor.dto.TimeSlotReservationDto;
import com.project.tableforyou.domain.reservationrefactor.redis.util.constants.ReservationConstants;
import com.project.tableforyou.domain.reservationrefactor.service.queue.QueueReservationCommandService;
import com.project.tableforyou.domain.reservationrefactor.service.timeslot.TimeSlotReservationCommandService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ReservationLockManager {
    private final RedissonClient redissonClient;
    private final QueueReservationCommandService queueReservationCommandService;
    private final TimeSlotReservationCommandService timeSlotReservationCommandService;

    // 락 이름 생성 시 사용하는 prefix들
    private static final String QUEUE = "queue:";
    private static final String TIME_SLOT = "timeslot:";
    private static final String LOCK = "lock:";

    private static final long WAIT_TIME = 5L;     // 락 획득을 기다리는 최대 시간 (초)
    private static final long LEASE_TIME = 5L;    // 락 소유 시간 (초)

    /**
     * 일반 대기열(Queue) 예약 저장 - Redisson FairLock 사용
     *
     * @param userId       사용자 ID
     * @param username     사용자 이름
     * @param restaurantId 음식점 ID
     * @return 예약 번호
     */
    public int saveQueueReservation(Long userId, String username, Long restaurantId) {
        // Lock 범위 : 가게
        String key = ReservationConstants.RESERVATION_KEY_PREFIX + QUEUE + restaurantId;
        RLock lock = redissonClient.getFairLock(LOCK + key);
        try {
            // 락 시도
            boolean available = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!available) {   // 획득 실패 시 (대기 시간 동안)
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_ERROR);
            }

            return queueReservationCommandService.create(userId, username, restaurantId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.THREAD_INTERRUPTED);
        } finally {
            // 현재 쓰레드가 락을 보유 중이면 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 시간대(TimeSlot) 예약 저장 - Redisson FairLock 사용
     *
     * @param userId       사용자 ID
     * @param username     사용자 이름
     * @param restaurantId 음식점 ID
     * @param createReq    예약 요청 정보 (날짜, 시간, 좌석 수 포함)
     */
    public void saveTimeSlotReservation(Long userId, String username, Long restaurantId, TimeSlotReservationDto.Create createReq) {
        // Lock 범위 : 가게 + 예약 날짜 + 시간대
        String key = ReservationConstants.RESERVATION_KEY_PREFIX
                + TIME_SLOT
                + restaurantId + ":"
                + createReq.date() + ":"
                + createReq.timeSlot();
        RLock lock = redissonClient.getFairLock(LOCK + key);

        try {
            // 락 시도
            boolean available = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!available) {   // 획득 실패 시 (대기 시간 동안)
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_ERROR);
            }

            timeSlotReservationCommandService.create(
                    userId, username, restaurantId,
                    createReq.availableSeats(), createReq.date(), createReq.timeSlot()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.THREAD_INTERRUPTED);
        } finally {
            // 현재 쓰레드가 락을 보유 중이면 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
