package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantInfoDto {

    private final String name;
    private final double rating;
    private final int ratingNum;
    private final String restaurantImage;
    private final String foodType;

    public RestaurantInfoDto(Restaurant restaurant) {
        this.name = restaurant.getName();
        this.rating = restaurant.getRating();
        this.ratingNum = restaurant.getRatingNum();
        this.restaurantImage = restaurant.getRestaurantImage();
        this.foodType = restaurant.getFoodType();
    }
}
