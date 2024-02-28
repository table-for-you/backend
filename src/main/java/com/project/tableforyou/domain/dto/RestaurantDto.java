package com.project.tableforyou.domain.dto;

import com.project.tableforyou.domain.entity.Restaurant;
import com.project.tableforyou.domain.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantDto {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private int usedSeats;
        private int totalSeats;
        private int likeCount;
        private double rating;
        @NotBlank
        private String time;
        @NotBlank
        private String name;
        @NotBlank
        private String location;
        @NotBlank
        private String tel;
        @NotBlank
        private String description;
        private User user;

        /* dto -> Entity */
        public Restaurant toEntity() {
            Restaurant restaurant = Restaurant.builder()
                    .usedSeats(usedSeats)
                    .totalSeats(totalSeats)
                    .likeCount(likeCount)
                    .rating(rating)
                    .time(time)
                    .name(name)
                    .location(location)
                    .tel(tel)
                    .description(description)
                    .user(user)
                    .build();

            return restaurant;
        }
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank
        private String time;
        @NotBlank
        private String name;
        @NotBlank
        private String location;
        @NotBlank
        private String tel;
        @NotBlank
        private String description;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final int usedSeats;
        private final int totalSeats;
        private final int likeCount;
        private final double rating;
        private final String time;
        private final String name;
        private final String location;
        private final String tel;
        private final String description;
        private final Long user_id;
        private final String created_time;
        private final String modified_time;
        private final int reservationSize;
        //private final List<ReservationDto.Response> reservations;
        //private final List<MenuDto.Response> menus;

        /* Entity -> dto */
        public Response(Restaurant restaurant) {
            this.id = restaurant.getId();
            this.usedSeats = restaurant.getUsedSeats();
            this.totalSeats = restaurant.getTotalSeats();
            this.likeCount = restaurant.getLikeCount();
            this.rating = restaurant.getRating();
            this.time = restaurant.getTime();
            this.name = restaurant.getName();
            this.location = restaurant.getLocation();
            this.tel = restaurant.getTel();
            this.description = restaurant.getDescription();
            this.user_id = restaurant.getUser().getId();
            this.created_time = restaurant.getCreated_time();
            this.modified_time = restaurant.getModified_time();
            this.reservationSize = restaurant.getReservations().size();
            //this.reservations = restaurant.getReservations().stream().map(ReservationDto.Response::new).collect(Collectors.toList());
            //this.menus = restaurant.getMenus().stream().map(MenuDto.Response::new).collect(Collectors.toList());
        }
    }
}
