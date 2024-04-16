package com.project.tableforyou.domain.reservation.entity;


import com.project.tableforyou.domain.reservation.dto.ReservationResponseDto;
import lombok.*;

@Getter @Setter
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
}
