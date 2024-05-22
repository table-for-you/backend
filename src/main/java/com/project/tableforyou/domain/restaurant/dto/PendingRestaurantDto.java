package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class PendingRestaurantDto {

    private final Long id;
    private final String name;
    private final String ownerName;

    public PendingRestaurantDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.ownerName = restaurant.getUser().getName();
    }
}
