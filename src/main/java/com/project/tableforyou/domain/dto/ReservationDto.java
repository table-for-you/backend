package com.project.tableforyou.domain.dto;

import com.project.tableforyou.domain.entity.Reservation;
import com.project.tableforyou.domain.entity.Restaurant;
import com.project.tableforyou.domain.entity.User;
import lombok.*;

public class ReservationDto {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private int booking;
        private User user;
        private Restaurant restaurant;

        /* dto -> Entity */
        public Reservation toEntity() {
            Reservation reservation = Reservation.builder()
                    .booking(booking)
                    .user(user)
                    .restaurant(restaurant)
                    .build();
            return reservation;
        }
    }


    @Getter
    public static class Response {
        private final int booking;
        private final Long user_id;
        private final Long store_id;
        private final String created_time;
        private final String modified_time;

        /* Entity -> dto */
        public Response(Reservation reservation) {
            this.booking = reservation.getBooking();
            this.user_id = reservation.getUser().getId();
            this.store_id = reservation.getRestaurant().getId();
            this.created_time = reservation.getCreated_time();
            this.modified_time = reservation.getModified_time();
        }
    }
}


