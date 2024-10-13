package com.project.tableforyou.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RestaurantNameSeatsDto {
    private String restaurantName;
    private int totalSeats;
}
