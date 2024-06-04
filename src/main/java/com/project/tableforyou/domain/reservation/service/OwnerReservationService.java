package com.project.tableforyou.domain.reservation.service;

import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerReservationService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public boolean isOwnerRestaurant(Long restaurantId, String ownerUsername) {

        return restaurantRepository.existsByIdAndUser_Username(restaurantId, ownerUsername);
    }
}
