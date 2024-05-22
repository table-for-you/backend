package com.project.tableforyou.domain.visit.dto;

import com.project.tableforyou.domain.restaurant.dto.RestaurantInfoDto;
import com.project.tableforyou.domain.visit.entity.Visit;
import lombok.Getter;

@Getter
public class VisitResponseDto {

    private final RestaurantInfoDto restaurantInfoDto;
    private final String visitTime;

    public VisitResponseDto(Visit visit) {
        this.restaurantInfoDto = new RestaurantInfoDto(visit.getRestaurant());
        this.visitTime = visit.getVisitTime();
    }
}
