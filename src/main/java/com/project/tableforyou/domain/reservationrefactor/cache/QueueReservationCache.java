package com.project.tableforyou.domain.reservationrefactor.cache;

public record QueueReservationCache(
        Long userId,
        String username,
        int reservationNumber
) {
    public static QueueReservationCache of(Long userId, String username, int reservationNumber) {
        return new QueueReservationCache(userId, username, reservationNumber);
    }
}
