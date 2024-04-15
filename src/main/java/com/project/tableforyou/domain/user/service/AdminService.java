package com.project.tableforyou.domain.user.service;

import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /* 전체 회원 불러오기 */
    @Transactional(readOnly = true)
    public Page<UserResponseDto> userPageList(Pageable pageable) {

        log.info("Finding all users");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserResponseDto::new);
    }

    /* 회원 삭제하기 */
    @Transactional
    public void deleteUser(Long user_id) {

        User user = userRepository.findById(user_id).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }

    /* 등록 처리 중인 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDto> handleRestaurantList(Pageable pageable) {

        Page<Restaurant> restaurants = restaurantRepository.findByStatus(RestaurantStatus.PENDING, pageable);
        return restaurants.map(RestaurantResponseDto::new);
    }

    /* 가게 등록하기 */
    @Transactional
    public void approvalRestaurant(Long restaurant_id) {

        Restaurant restaurant = restaurantRepository.findById(restaurant_id).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.statusUpdate(RestaurantStatus.APPROVED);
    }

    /* 가게 삭제하기 */
    @Transactional
    public void deleteRestaurant(Long restaurant_id) {

        Restaurant restaurant = restaurantRepository.findById(restaurant_id).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurantRepository.delete(restaurant);
    }
}
