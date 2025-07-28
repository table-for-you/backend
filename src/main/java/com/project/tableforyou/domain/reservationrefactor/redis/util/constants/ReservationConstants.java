package com.project.tableforyou.domain.reservationrefactor.redis.util.constants;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;

public final class ReservationConstants {

    private ReservationConstants() { }

    public static final String RESERVATION_KEY_PREFIX = "reservation:";
    public static final String RESERVATION_QUEUE_RESTAURANT_SET_KEY = "reservation:restaurants";

    private static final String QUEUE_RESERVATION_KEY = "queue:reserved::%d";
    private static final String QUEUE_RESERVATION_QUEUE_KEY = "reservation:queue::%d";
    private static final String QUEUE_RESERVATION_NUMBER_KEY = "queue:reservation-number::%d";
    private static final String QUEUE_ENTERED_COUNT_KEY = "queue:entered:count::%d";
    private static final String QUEUE_CANCELED_COUNT_KEY = "queue:canceled:count::%d";
    private static final String QUEUE_SUCCESS_COUNT_KEY = "reservation:success:%d";
    private static final String QUEUE_ENTRY_COUNT_KEY = "reservation:entry:%d";
    private static final String TIME_SLOT_RESERVATION_KEY = "timeslot:reserved::%d:%s:%s";
    private static final String TIME_SLOT_RESERVATION_NUMBER_KEY = "timeslot:reservation-number:%d:%s:%s";

    /**
     * queue 예약 key
     */
    public static String getQueueReservationKey(Long restaurantId) {
        return String.format(QUEUE_RESERVATION_KEY, restaurantId);
    }

    /**
     * queue 대기열 key
     */
    public static String getQueueReservationQueueKey(Long restaurantId) {
        return String.format(QUEUE_RESERVATION_QUEUE_KEY, restaurantId);
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

    /**
     * 예약 성공 카운트 key
     */
    public static String getQueueSuccessCountKey(Long restaurantId) {
        return String.format(QUEUE_SUCCESS_COUNT_KEY, restaurantId);
    }

    /**
     * 입장 허용된 인원 수 카운트 key
     */
    public static String getQueueEntryCountKey(Long restaurantId) {
        return String.format(QUEUE_ENTRY_COUNT_KEY, restaurantId);
    }
}
