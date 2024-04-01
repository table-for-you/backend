package com.project.tableforyou.domain.restaurant.service;

import com.project.tableforyou.domain.Role;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.CustomException;
import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    /* 가게 create. ADMIN을 주인으로 생성. ADMIN이 선별하여 주인 변경 예정 */
    @Transactional
    public Long save(String username, RestaurantRequestDto dto) {

        log.info("Creating Restaurant by user username: {}", username);
        User user = userRepository.findByRole(Role.ADMIN).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        dto.setUser(user);
        dto.setUsername(username);
        Restaurant restaurant = dto.toEntity();
        restaurantRepository.save(restaurant);

        log.info("Restaurant created with ID: {}", restaurant.getId());
        return restaurant.getId();
    }

    /* 가게 읽기 */
    @Transactional(readOnly = true)
    public RestaurantResponseDto findByName(String name) {

        log.info("Finding restaurant by name: {}", name);
        Restaurant restaurant = restaurantRepository.findByName(name).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
        return new RestaurantResponseDto(restaurant);
    }


    /* 가게 리스트 페이징. 등록된 가게만 들고오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDto> RestaurantPageList(Pageable pageable) {

        log.info("Finding all restaurants");
        User user = userRepository.findByRole(Role.ADMIN).orElseThrow(() ->         // ADMIN 계정
                new CustomException(ErrorCode.USER_NOT_FOUND));
        // ADMIN 계정으로 등록되어 있는(아직 등록처리 안된) 가게를 제외한 가게 불러오기.
        Page<Restaurant> restaurants = restaurantRepository.findByUserNot(user, pageable);
        return restaurants.map(RestaurantResponseDto::new);
    }

    /* 가게 검색 || 가게 소개 검색 페이징 */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDto> RestaurantPageSearchList(String searchKeyword1, String searchKeyword2, Pageable pageable) {

        log.info("Finding all restaurants with searchKeyword: {}", searchKeyword1);
        Page<Restaurant> restaurants = restaurantRepository.findByNameContainingOrDescriptionContaining(searchKeyword1, searchKeyword2, pageable);
        return restaurants.map(RestaurantResponseDto::new);
    }

    /* 가게 좌석 업데이트 */
    @Transactional
    public void updateUsedSeats(String restaurant, int value) {    // 가게에 user를 추가해야 하지 않나? 그리고 인원이 줄면 어떻게 user을 없애지? 그리고 예약자를 줄이고 여기로 다시 보내야하는데
        restaurantRepository.updateUsedSeats(restaurant, value);
        log.info("Restaurant usedSeat updated successfully with restaurant: {}", restaurant);
    }

    /* 좋아요 업데이트 */
    @Transactional
    public void updateLikeCount(String restaurant, int value) {
        restaurantRepository.updateLikeCount(restaurant, value);
        log.info("Restaurant likeCount updated successfully with restaurant: {}", restaurant);
    }

    /* 평점 업데이트 */
    @Transactional
    public void updateRating(String name, double rating) {

        Restaurant restaurant = restaurantRepository.findByName(name).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
        double before_rating = restaurant.getRating();
        int now_ratingNum = restaurant.getRating_num() + 1;

        double now_rating = 0.0;
        if(now_ratingNum == 1)
            now_rating = rating;
        else
            now_rating = before_rating + (rating - before_rating) / now_ratingNum;  // 누적 평균 공식.


        restaurant.updateRating(now_rating, now_ratingNum);
        log.info("Restaurant rating updated successfully with name: {}", name);
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

    /* 자신의 권한인지 확인 */
    private boolean verifyAuthenticationByUsername(String expectedUsername, String actualUsername) {
        return actualUsername.equals(expectedUsername);
    }
}