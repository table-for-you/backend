package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantResponseDto {

    private final Long id;
    private final int usedSeats;
    private final int totalSeats;
    private final double rating;
    private final String time;
    private final String name;
    private final Region region;
    private final String location;
    private final String tel;
    private final String description;
    private final Long userId;
    private final int likeCount;
    private final String createdTime;
    private final String modifiedTime;

    /* Entity -> dto */
    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.usedSeats = restaurant.getUsedSeats();
        this.totalSeats = restaurant.getTotalSeats();
        this.rating = restaurant.getRating();
        this.time = restaurant.getTime();
        this.name = restaurant.getName();
        this.region = restaurant.getRegion();
        this.location = restaurant.getLocation();
        this.tel = restaurant.getTel();
        this.description = restaurant.getDescription();
        this.userId = restaurant.getUser().getId();
        this.likeCount = restaurant.getLikes().size();
        this.createdTime = restaurant.getCreatedTime();
        this.modifiedTime = restaurant.getModifiedTime();
    }
}
