package com.project.tableforyou.domain.reservationrefactor.dto;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;

import java.time.LocalDate;

public class TimeSlotReservationDto {

    public record Create(
            int availableSeats,
            LocalDate date,
            TimeSlot timeSlot
    ) { }

    public record Request(
            LocalDate date,
            TimeSlot timeSlot
    ) { }
}
