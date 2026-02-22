package com.project.tableforyou.domain.reservationrefactor.type;

public enum FailReason {
    NONE,           // 성공
    DUPLICATE,      // 중복 예약 요청
    LOCK_TIMEOUT,   // 락 획득 실패
    EXPIRED,        // 예약 시간 타임 아웃
    SYSTEM          // 시세틈 오류
}
