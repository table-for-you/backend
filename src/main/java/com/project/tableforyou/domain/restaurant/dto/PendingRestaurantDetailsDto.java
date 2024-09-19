package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.image.entity.Image;
import com.project.tableforyou.domain.restaurant.entity.FoodType;
import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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
    private final String mainImage;
    private final List<String> subImages;
    private final FoodType foodType;

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
        this.mainImage = restaurant.getMainImage();
        this.subImages = restaurant.getImages().stream().map(Image::getUrl).collect(Collectors.toList());
        this.foodType = restaurant.getFoodType();
    }
}
