package com.project.tableforyou.domain.reservation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSlotReservation {

    private String username;
    private TimeSlot timeSlot;
}
