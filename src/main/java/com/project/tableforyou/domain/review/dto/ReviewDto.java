package com.project.tableforyou.domain.review.dto;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.review.entity.Review;
import com.project.tableforyou.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(name = "ReviewDto", description = "리뷰 작성 DTO")
public class ReviewDto {

    @Min(value = 0, message = "최저 점수는 0점입니다.")
    @Max(value = 5, message = "최대 점수는 5점입니다.")
    @Schema(description = "별점", example = "3.5")
    private double rating;

    @Schema(description = "리뷰 내용", example = "맛있어요!")
    private String content;

    public Review toEntity(User user, Restaurant restaurant) {
        return Review.builder()
                .rating(rating)
                .content(content)
                .restaurantId(restaurant.getId())
                .nickname(user.getNickname())
                .user(user)
                .restaurant(restaurant)
                .restaurantName(restaurant.getName())
                .build();
    }
}
