package com.project.tableforyou.domain.reservation.dto;

import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import lombok.Getter;

@Getter
public class QueueReservationResDto {

    private final int booking;
    private final String username;

    /* Entity -> dto */
    public QueueReservationResDto(QueueReservation queueReservation) {
        this.booking = queueReservation.getBooking();
        this.username = queueReservation.getUsername();
    }
}
