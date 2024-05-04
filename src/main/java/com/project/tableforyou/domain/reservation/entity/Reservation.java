package com.project.tableforyou.domain.reservation.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {


    // 최대 예약 건수 설정을 해야할 듯.
    private int booking;

    private String username;

    private String restaurant;


    public void update(int booking) {
        this.booking = booking;
    }
    public void setBooking(int booking) {
        this.booking = booking;
    }
}
