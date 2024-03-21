package com.project.tableforyou.domain.dto;

import com.project.tableforyou.domain.entity.Reservation;
import lombok.*;

public class ReservationDto {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private int booking;
        private String username;
        private String restaurant;

        /* dto -> Entity */
        public Reservation toEntity() {
            return Reservation.builder()
                    .booking(booking)
                    .username(username)
                    .restaurant(restaurant)
                    .build();
        }
    }

    @Getter
    public static class Response {
        private final int booking;
        private final String username;
        private final String restaurant;

        /* Entity -> dto */
        public Response(Reservation reservation) {
            this.booking = reservation.getBooking();
            this.username = reservation.getUsername();
            this.restaurant = reservation.getRestaurant();
        }
    }
}


