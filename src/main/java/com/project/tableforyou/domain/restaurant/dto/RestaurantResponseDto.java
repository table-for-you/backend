package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RestaurantResponseDto {

    private final Long id;
    private final Long ownerId;
    private final int usedSeats;
    private final int totalSeats;
    private final double rating;
    private final String time;
    private final String name;
    private final Region region;
    private final String location;
    private final double latitude;
    private final double longitude;
    private final String tel;
    private final String description;
    private final String restaurantImage;
    private final String foodType;
    private final boolean isParking;
    private final int likeCount;

    /* Entity -> dto */
    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.ownerId = restaurant.getUser().getId();
        this.usedSeats = restaurant.getUsedSeats();
        this.totalSeats = restaurant.getTotalSeats();
        this.rating = restaurant.getRating();
        this.time = restaurant.getTime();
        this.name = restaurant.getName();
        this.region = restaurant.getRegion();
        this.location = restaurant.getLocation();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.tel = restaurant.getTel();
        this.description = restaurant.getDescription();
        this.restaurantImage = restaurant.getRestaurantImage();
        this.foodType = restaurant.getFoodType();
        this.isParking = restaurant.isParking();
        this.likeCount = restaurant.getLikes().size();
    }
}
