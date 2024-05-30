package com.project.tableforyou.domain.restaurant.service;

import com.project.tableforyou.aop.annotation.VerifyAuthentication;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.domain.common.service.AssociatedEntityService;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerRestaurantService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AssociatedEntityService associatedEntityService;

    /* 가게 create. 가게 등록 대기 상태. */
    @Transactional
    public Long saveRestaurant(String username, RestaurantRequestDto restaurantDto) {

        log.info("Creating Restaurant by user username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        restaurantDto.setUser(user);
        Restaurant restaurant = restaurantDto.toEntity();
        restaurantRepository.save(restaurant);

        log.info("Restaurant created with ID: {}", restaurant.getId());
        return restaurant.getId();
    }

    /* 사장 가게 불러오기 */
    @Transactional(readOnly = true)
    public List<RestaurantNameDto> findByRestaurantOwner(String username) {

        List<Restaurant> restaurants = restaurantRepository.findByUser_Username(username);
        return restaurants.stream().map(RestaurantNameDto::new).collect(Collectors.toList());
    }

    /* 가게 수정 */
    @VerifyAuthentication
    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantUpdateDto restaurantUpdateDto) {

        log.info("Updating Restaurant with name: {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.update(restaurantUpdateDto);
        log.info("Restaurant updated successfully with name: {}", restaurantId);

    }


    /* 가게 삭제 */
    @VerifyAuthentication
    @Transactional
    public void deleteRestaurant(Long restaurantId) {         // 다른 사용자가 삭제하는 경우 확인해보기. 만약 그런다면 findByUserIdAndId 사용. 그냥 권한 설정 하면 될듯?

        log.info("Deleting Restaurant with name: {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        associatedEntityService.deleteAllLikeByRestaurant(restaurant);  // 가게의 좋아요 삭제
        associatedEntityService.deleteAllMenuByRestaurant(restaurant);  // 가게의 메뉴 삭제
        associatedEntityService.deleteAllVisitByRestaurant(restaurant); // 방문객 삭제

        restaurantRepository.delete(restaurant);
        log.info("Restaurant deleted successfully with name: {}", restaurantId);
    }
}
