package com.project.tableforyou.domain.review.service;

import com.project.tableforyou.common.aop.annotation.ReviewId;
import com.project.tableforyou.common.aop.annotation.VerifyAuthentication;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.domain.restaurant.service.RestaurantService;
import com.project.tableforyou.domain.review.dto.ReviewDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.review.dto.ReviewResDto;
import com.project.tableforyou.domain.review.dto.ReviewUpdateDto;
import com.project.tableforyou.domain.review.entity.Review;
import com.project.tableforyou.domain.review.repository.ReviewRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;

    /* 리뷰 생성 */
    @Transactional
    public void createReview(Long userId, Long restaurantId, ReviewDto reviewDto) {

        if (reviewRepository.existsByUser_IdAndRestaurant_Id(userId, restaurantId))
            throw new CustomException(ErrorCode.ALREADY_REVIEW_RESTAURANT);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurantService.updateRating(restaurant, reviewDto.getRating());

        reviewRepository.save(reviewDto.toEntity(user, restaurant));
    }

    /* 사용자 참여 리뷰 불러오기 */
    @Transactional(readOnly = true)
    public List<ReviewResDto> getReviewByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUser_Id(userId);

        return reviews.stream().map(ReviewResDto::new).collect(Collectors.toList());
    }

    /* 가게 리뷰 불러오기 */
    @Transactional(readOnly = true)
    public List<ReviewResDto> getReviewByRestaurantId(Long restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurant_Id(restaurantId);

        return reviews.stream().map(ReviewResDto::new).collect(Collectors.toList());
    }

    /* 리뷰 업데이트 */
    @VerifyAuthentication
    @Transactional
    public void updateReview(@ReviewId Long reviewId, Long restaurantId, ReviewUpdateDto reviewUpdateDto) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        double preRating = restaurant.getRating() * restaurant.getRatingNum();
        double newRating =
                (preRating - reviewUpdateDto.getPreRating() + reviewUpdateDto.getRating()) / restaurant.getRatingNum();
        restaurant.updateRating(newRating);

        review.updateReview(reviewUpdateDto.getRating(), reviewUpdateDto.getContent());
    }

    /* 리뷰 삭제 */
    @VerifyAuthentication
    @Transactional
    public void deleteReview(@ReviewId Long reviewId, Long restaurantId, double rating) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        double preRating = restaurant.getRating() * restaurant.getRatingNum();
        double newRating = (preRating - rating) / (restaurant.getRatingNum() - 1);
        restaurant.updateRating(newRating, restaurant.getRatingNum() - 1);

        reviewRepository.deleteById(reviewId);
    }
}
