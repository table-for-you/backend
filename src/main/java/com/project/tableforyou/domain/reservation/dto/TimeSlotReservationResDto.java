package com.project.tableforyou.domain.reservation.dto;

import com.project.tableforyou.domain.reservation.entity.TimeSlotReservation;
import lombok.Getter;

@Getter
public class TimeSlotReservationResDto {

    private final String username;

    /* Entity -> dto */
    public TimeSlotReservationResDto(TimeSlotReservation timeSlotReservation) {
        this.username = timeSlotReservation.getUsername();
    }
}
