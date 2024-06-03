package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantNameDto {

    private final Long id;
    private final String name;

    public RestaurantNameDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
    }
}
