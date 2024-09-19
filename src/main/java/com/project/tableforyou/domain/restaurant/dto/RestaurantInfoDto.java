package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.FoodType;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantInfoDto {

    private final Long id;
    private final String name;
    private final double rating;
    private final int ratingNum;
    private final boolean isParking;
    private final String mainImage;
    private final FoodType foodType;

    public RestaurantInfoDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.rating = restaurant.getRating();
        this.ratingNum = restaurant.getRatingNum();
        this.isParking = restaurant.isParking();
        this.mainImage = restaurant.getMainImage();
        this.foodType = restaurant.getFoodType();
    }
}
