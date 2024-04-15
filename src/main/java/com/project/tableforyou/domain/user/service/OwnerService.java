package com.project.tableforyou.domain.user.service;

import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /* 가게 create. 가게 등록 대기 상태. */
    @Transactional
    public Long save(String username, RestaurantRequestDto dto) {

        log.info("Creating Restaurant by user username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        dto.setUser(user);
        Restaurant restaurant = dto.toEntity();
        restaurantRepository.save(restaurant);

        log.info("Restaurant created with ID: {}", restaurant.getId());
        return restaurant.getId();
    }

    /* 가게 수정 */
    @Transactional
    public void update(String name, String username, RestaurantUpdateDto dto) {

        log.info("Updating Restaurant with name: {}", name);
        Restaurant restaurant = restaurantRepository.findByName(name).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
        if(!verifyAuthenticationByUsername(username, restaurant.getUser().getUsername()))
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        else {
            restaurant.update(dto);
            log.info("Restaurant updated successfully with name: {}", name);
        }
    }


    /* 가게 삭제 */
    @Transactional
    public void delete(String name, String username) {         // 다른 사용자가 삭제하는 경우 확인해보기. 만약 그런다면 findByUserIdAndId 사용. 그냥 권한 설정 하면 될듯?

        log.info("Deleting Restaurant with name: {}", name);
        Restaurant restaurant = restaurantRepository.findByName(name).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if(!verifyAuthenticationByUsername(username, restaurant.getUser().getUsername()))
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        else {
            restaurantRepository.delete(restaurant);
            log.info("Restaurant deleted successfully with name: {}", name);
        }
    }

    /* 자신의 권한인지 확인 */
    private boolean verifyAuthenticationByUsername(String expectedUsername, String actualUsername) {
        return actualUsername.equals(expectedUsername);
    }

    /* 가게 등록 오류 확인 */
    public Map<String, String> validateHandler(Errors errors) {
        Map<String, String> validateResult = new HashMap<>();

        for (FieldError error: errors.getFieldErrors()) {
            validateResult.put(error.getField(), error.getDefaultMessage());
        }
        return validateResult;
    }
}
