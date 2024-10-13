package com.project.tableforyou.domain.restaurant.service;

import com.project.tableforyou.domain.restaurant.dto.RestaurantInfoDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.restaurant.entity.FoodType;
import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    /* 가게 읽기 */
    @Transactional(readOnly = true)
    public RestaurantResponseDto readRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
        return new RestaurantResponseDto(restaurant);
    }

    @Transactional(readOnly = true)
    public int readRestaurantUsedSeats(Long restaurantId) {
        return restaurantRepository.getRestaurantUsedSeatsByRestaurantId(restaurantId);
    }

    /* 가게 리스트 페이징. 등록된 가게만 들고오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantInfoDto> readAllRestaurant(Pageable pageable) {

        Page<Restaurant> restaurants = restaurantRepository.findByStatus(RestaurantStatus.APPROVED, pageable);
        return restaurants.map(RestaurantInfoDto::new);
    }

    /* 지역별 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantInfoDto> readAllRestaurantByRegion(String region, Pageable pageable) {

        Page<Restaurant> restaurants =
                restaurantRepository.findByRegionAndStatus(Region.valueOf(region), RestaurantStatus.APPROVED, pageable);
        return restaurants.map(RestaurantInfoDto::new);
    }

    /* 주소로 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantInfoDto> readAllRestaurantByLocation(String searchKeyword, Pageable pageable) {

        Page<Restaurant> restaurants =
                restaurantRepository.findByLocationContainingAndStatus(searchKeyword, RestaurantStatus.APPROVED, pageable);
        return restaurants.map(RestaurantInfoDto::new);
    }

    /* 음식 종류로 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantInfoDto> readAllRestaurantByFoodType(String foodType, Pageable pageable) {

        Page<Restaurant> restaurants  =
                restaurantRepository.findByFoodTypeAndStatus(FoodType.valueOf(foodType), RestaurantStatus.APPROVED, pageable);
        return restaurants.map(RestaurantInfoDto::new);
    }

    /* 가게 검색 || 가게 소개 검색 페이징 */
    @Transactional(readOnly = true)
    public Page<RestaurantInfoDto> restaurantPageSearchList(String searchKeyword, Pageable pageable) {

        Page<Restaurant> restaurants = restaurantRepository.
                findByStatusAndNameContainingOrDescriptionContaining(RestaurantStatus.APPROVED, searchKeyword, searchKeyword, pageable);
        return restaurants.map(RestaurantInfoDto::new);
    }

    @Transactional
    public void updateUsedSeats(Long restaurantId, int value) {
        RestaurantResponseDto restaurantDto = readRestaurant(restaurantId);

        if (value == 1 && restaurantDto.getUsedSeats() < restaurantDto.getTotalSeats()) {   // 좌석 증가 가능
            updateUsedSeatsById(restaurantId, value);
        } else if (value == -1 && restaurantDto.getUsedSeats() > 0) {                       // 좌성 감소 가능
            updateUsedSeatsById(restaurantId, value);
        } else {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
    }

    /* 가게 좌석 업데이트 */
    private void updateUsedSeatsById(Long restaurantId, int value) {
        restaurantRepository.updateUsedSeats(restaurantId, value);
    }

    /* 평점 업데이트 */
    @Transactional
    public void updateRating(Restaurant restaurant, double rating) {

        double beforeRating = restaurant.getRating();
        int nowRatingNum = restaurant.getRatingNum() + 1;

        double nowRating = 0.0;
        if (nowRatingNum == 1)
            nowRating = rating;
        else
            nowRating = beforeRating + (rating - beforeRating) / nowRatingNum;  // 누적 평균 공식.


        restaurant.updateRating(nowRating, nowRatingNum);
    }
}