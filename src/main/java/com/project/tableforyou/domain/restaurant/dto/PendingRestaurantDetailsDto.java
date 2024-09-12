package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class PendingRestaurantDetailsDto {

    private final Long id;
    private final String time;
    private final String name;
    private final Region region;
    private final String location;
    private final double latitude;
    private final double longitude;
    private final String tel;
    private final String description;
    private final boolean isParking;
    private final String restaurantImage;
    private final String businessLicenseImage;
    private final String foodType;

    public PendingRestaurantDetailsDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.time = restaurant.getTime();
        this.name = restaurant.getName();
        this.region = restaurant.getRegion();
        this.location = restaurant.getLocation();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.tel = restaurant.getTel();
        this.description = restaurant.getDescription();
        this.isParking = restaurant.isParking();
        this.restaurantImage = restaurant.getRestaurantImage();
        this.businessLicenseImage = restaurant.getBusinessLicenseImage();
        this.foodType = restaurant.getFoodType();
    }
}
