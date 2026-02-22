package com.project.tableforyou.domain.reservationrefactor.dto;

import com.project.tableforyou.domain.reservationrefactor.type.AttemptStatus;
import com.project.tableforyou.domain.reservationrefactor.type.FailReason;

public class QueueEvents {

    /**
     * 유저가 대기열에 입장했음을 알리는 이벤트
     */
    public record QueueJoined(
            Long restaurantId,
            Long userId
    ) {
    }

    /**
     * 2분 TTL 후 발생하는 timeout 이벤트
     */
    public record AttemptTimeout(
            String attemptId,
            Long restaurantId,
            Long userId
    ) {
    }

    /**
     * 예약 시도 종료 이벤트 (SUCCESS / FAIL / TIMEOUT)
     */
    public record AttemptFinished(
            String attemptId,
            Long restaurantId,
            Long userId,
            AttemptStatus status,
            FailReason failReason
    ) {
    }

    /**
     * SSE 전송용 payload
     */
    public record QueueStatusPayload(
            boolean canEnter,

            // canEnter = true
            String attemptId,
            long ttlSec,

            // canEnter = false
            long myPosition,
            long totalWaiting
    ) {
    }
}