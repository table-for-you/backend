package com.project.tableforyou.domain.reservationrefactor.redis.util.constants;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;

public final class ReservationConstants {

    private ReservationConstants() { }

    public static final String RESERVATION_KEY_PREFIX = "reservation:";

    private static final String QUEUE_RESERVATION_KEY = "queue:reserved::%d";
    private static final String QUEUE_RESERVATION_NUMBER_KEY = "queue:reservation-number::%d";
    private static final String QUEUE_ENTERED_COUNT_KEY = "queue:entered:count::%d";
    private static final String QUEUE_CANCELED_COUNT_KEY = "queue:canceled:count::%d";
    private static final String TIME_SLOT_RESERVATION_KEY = "timeslot:reserved::%d:%s:%s";
    private static final String TIME_SLOT_RESERVATION_NUMBER_KEY = "timeslot:reservation-number:%d:%s:%s";

    /**
     * queue 예약 key
     */
    public static String getQueueReservationKey(Long restaurantId) {
        return String.format(QUEUE_RESERVATION_KEY, restaurantId);
    }

    /**
     * queue 예약 번호 key
     */
    public static String getQueueReservationNumberKey(Long restaurantId) {
        return String.format(QUEUE_RESERVATION_NUMBER_KEY, restaurantId);
    }

    /**
     * time slot 예약 키
     */
    public static String getTimeSlotReservationKey(Long restaurantId, String date, TimeSlot timeSlot) {
        return String.format(TIME_SLOT_RESERVATION_KEY, restaurantId, date, timeSlot);
    }

    /**
     * time slot 예약 번호 key
     */
    public static String getTimeSlotReservationNumberKey(Long restaurantId, String date, TimeSlot timeSlot) {
        return String.format(TIME_SLOT_RESERVATION_NUMBER_KEY, restaurantId, date, timeSlot);
    }

    /**
     * 입장 카운트 key
     */
    public static String getQueueEnteredCountKey(Long restaurantId) {
        return String.format(QUEUE_ENTERED_COUNT_KEY, restaurantId);
    }

    /**
     * 취소 카운트 key
     */
    public static String getQueueCanceledCountKey(Long restaurantId) {
        return String.format(QUEUE_CANCELED_COUNT_KEY, restaurantId);
    }
}
