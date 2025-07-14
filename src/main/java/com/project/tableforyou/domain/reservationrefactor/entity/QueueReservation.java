package com.project.tableforyou.domain.reservationrefactor.entity;

import com.project.tableforyou.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Entity(name = "queue_reservation")
public class QueueReservation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    // 예약 날짜
    @Column(name = "date", nullable = false)
    private LocalDate date;

    // 대기순번 (1번부터 시작)
    @Column(name = "reservation_number")
    private int reservationNumber;

    // 예약 취소 여부
    @Column(name = "is_canceled", nullable = false)
    private boolean isCanceled = false;

    // 입장 완료 여부
    @Column(name = "is_entered", nullable = false)
    private boolean isEntered = false;

    // 취소 시각
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    // 입장 시각
    @Column(name = "entered_at")
    private LocalDateTime enteredAt;

    @Builder
    public QueueReservation(Long userId, String username, Long restaurantId, LocalDate date, int reservationNumber) {
        this.userId = userId;
        this.username = username;
        this.restaurantId = restaurantId;
        this.date = date;
        this.reservationNumber = reservationNumber;
    }

    public void cancelReservation() {
        if (this.isCanceled) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        if (this.isEntered) {
            throw new IllegalStateException("이미 입장한 예약은 취소할 수 없습니다.");
        }

        this.isCanceled = true;
        this.canceledAt = LocalDateTime.now();
    }

    public void enterRestaurant() {
        if (this.isCanceled) {
            throw new IllegalStateException("취소된 예약은 입장할 수 없습니다.");
        }

        if (this.isEntered) {
            throw new IllegalStateException("이미 입장한 예약입니다.");
        }

        this.isEntered = true;
        this.enteredAt = LocalDateTime.now();
    }
}
