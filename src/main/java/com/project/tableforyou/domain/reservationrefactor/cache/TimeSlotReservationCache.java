package com.project.tableforyou.domain.reservationrefactor.cache;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;

public record TimeSlotReservationCache(
        Long userId,
        String username,
        TimeSlot timeSlot
) {
    public static TimeSlotReservationCache of(Long userId, String username, TimeSlot timeSlot) {
        return new TimeSlotReservationCache(userId, username, timeSlot);
    }
}
