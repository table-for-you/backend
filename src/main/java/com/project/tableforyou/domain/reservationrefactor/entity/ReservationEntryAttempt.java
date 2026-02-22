package com.project.tableforyou.domain.reservationrefactor.entity;

import com.project.tableforyou.domain.reservationrefactor.type.AttemptStatus;
import com.project.tableforyou.domain.reservationrefactor.type.FailReason;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "reservation_entry_attempt")
public class ReservationEntryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_id", nullable = false, unique = true)
    private String attemptId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttemptStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "fail_reason", nullable = false)
    private FailReason failReason;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Builder
    public ReservationEntryAttempt(
            String attemptId,
            Long userId,
            Long restaurantId,
            LocalDate date,
            AttemptStatus status,
            FailReason failReason,
            LocalDateTime expiresAt
    ) {
        this.attemptId = attemptId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.date = date;
        this.status = status;
        this.failReason = failReason;
        this.expiresAt = expiresAt;
    }

    public void markSuccess() {
        if (isFinished()) return;
        this.status = AttemptStatus.SUCCESS;
        this.failReason = FailReason.NONE;
        this.finishedAt = LocalDateTime.now();
    }

    public void markFail(FailReason reason) {
        if (isFinished()) return;
        this.status = AttemptStatus.FAIL;
        this.failReason = (reason == null ? FailReason.SYSTEM : reason);
        this.finishedAt = LocalDateTime.now();
    }

    public void markTimeout() {
        if (isFinished()) return;
        this.status = AttemptStatus.TIMEOUT;
        this.failReason = FailReason.EXPIRED;
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return status == AttemptStatus.SUCCESS
                || status == AttemptStatus.FAIL
                || status == AttemptStatus.TIMEOUT;
    }
}