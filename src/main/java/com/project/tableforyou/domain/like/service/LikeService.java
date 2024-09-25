package com.project.tableforyou.domain.like.service;

import com.project.tableforyou.domain.like.entity.Like;
import com.project.tableforyou.domain.like.repository.LikeRepository;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /* 가게 좋아요 실행 메서드 */
    @Transactional
    public void likeRestaurant(String username, Long restaurantId) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (likeRepository.existsByUserAndRestaurant(user, restaurant)) {
            throw new CustomException(ErrorCode.ALREADY_LIKE_RESTAURANT);
        }

        Like like = Like.builder()
                .user(user)
                .restaurant(restaurant)
                .build();

        likeRepository.save(like);
    }

    /* 가게 좋아요 취소 메서드 */
    @Transactional
    public void unLikeRestaurant(String username, Long restaurantId) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        Like like = likeRepository.findByUserAndRestaurant(user, restaurant).orElseThrow(() ->
                new CustomException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);
    }

    /* 사용자가 좋아요한 가게 불러오기. */
    @Transactional(readOnly = true)
    public List<RestaurantNameDto> getRestaurantLike(String username) {

        List<Like> likes = likeRepository.findByUser_Username(username);

        return likes.stream()
                .map(like -> new RestaurantNameDto(like.getRestaurant()))
                .collect(Collectors.toList());
    }
}
