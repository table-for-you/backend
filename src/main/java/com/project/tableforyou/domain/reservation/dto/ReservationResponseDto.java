package com.project.tableforyou.domain.reservation.dto;

import com.project.tableforyou.domain.reservation.entity.Reservation;
import lombok.Getter;

@Getter
public class ReservationResponseDto {

    private final int booking;
    private final String username;
    private final String restaurant;

    /* Entity -> dto */
    public ReservationResponseDto(Reservation reservation) {
        this.booking = reservation.getBooking();
        this.username = reservation.getUsername();
        this.restaurant = reservation.getRestaurant();
    }
}
