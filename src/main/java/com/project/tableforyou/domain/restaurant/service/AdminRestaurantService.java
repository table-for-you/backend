package com.project.tableforyou.domain.restaurant.service;

import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminRestaurantService {

    private final RestaurantRepository restaurantRepository;

    /* 등록 처리 중인 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDto> handleRestaurantList(Pageable pageable) {

        Page<Restaurant> restaurants = restaurantRepository.findByStatus(RestaurantStatus.PENDING, pageable);
        return restaurants.map(RestaurantResponseDto::new);
    }

    /* 가게 등록하기 */
    @Transactional
    public void approvalRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.statusUpdate(RestaurantStatus.APPROVED);
    }

    /* 가게 삭제하기 */
    @Transactional
    public void deleteRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurantRepository.delete(restaurant);
    }
}
