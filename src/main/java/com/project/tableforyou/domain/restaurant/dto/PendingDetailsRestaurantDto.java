package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class PendingDetailsRestaurantDto {

    private final String time;
    private final String name;
    private final Region region;
    private final String location;
    private final String tel;
    private final String description;
    private final String restaurantImage;
    private final String businessLicenseImage;
    private final String foodType;

    public PendingDetailsRestaurantDto(Restaurant restaurant) {
        this.time = restaurant.getTime();
        this.name = restaurant.getName();
        this.region = restaurant.getRegion();
        this.location = restaurant.getLocation();
        this.tel = restaurant.getTel();
        this.description = restaurant.getDescription();
        this.restaurantImage = restaurant.getRestaurantImage();
        this.businessLicenseImage = restaurant.getBusinessLicenseImage();
        this.foodType = restaurant.getFoodType();
    }
}
