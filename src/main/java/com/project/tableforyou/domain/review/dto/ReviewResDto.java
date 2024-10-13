package com.project.tableforyou.domain.review.dto;

import com.project.tableforyou.domain.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResDto {

    private final Long reviewId;
    private final double rating;
    private final String content;
    private final Long restaurantId;
    private final String nickname;

    public ReviewResDto(Review review) {
        this.reviewId = review.getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.restaurantId = review.getRestaurantId();
        this.nickname = review.getNickname();
    }
}
