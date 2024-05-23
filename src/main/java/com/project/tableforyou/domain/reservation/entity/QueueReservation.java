package com.project.tableforyou.domain.reservation.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class QueueReservation {

    private int booking;
    private String username;


    public void updateBooking(int booking) {
        this.booking = booking;
    }

}
