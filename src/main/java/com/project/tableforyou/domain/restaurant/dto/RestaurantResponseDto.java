package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantResponseDto {

    private final Long id;
    private final int usedSeats;
    private final int totalSeats;
    private final int likeCount;
    private final double rating;
    private final String time;
    private final String username;
    private final String name;
    private final String location;
    private final String tel;
    private final String description;
    private final Long user_id;
    private final String created_time;
    private final String modified_time;

    /* Entity -> dto */
    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.usedSeats = restaurant.getUsedSeats();
        this.totalSeats = restaurant.getTotalSeats();
        this.likeCount = restaurant.getLikeCount();
        this.rating = restaurant.getRating();
        this.time = restaurant.getTime();
        this.username = restaurant.getUsername();
        this.name = restaurant.getName();
        this.location = restaurant.getLocation();
        this.tel = restaurant.getTel();
        this.description = restaurant.getDescription();
        this.user_id = restaurant.getUser().getId();
        this.created_time = restaurant.getCreated_time();
        this.modified_time = restaurant.getModified_time();
    }
}
