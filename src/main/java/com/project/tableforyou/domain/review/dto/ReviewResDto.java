package com.project.tableforyou.domain.review.dto;

import com.project.tableforyou.domain.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResDto {

    private final double rating;
    private final String content;
    private final Long restaurantId;

    public ReviewResDto(Review review) {
        this.rating = review.getRating();
        this.content = review.getContent();
        this.restaurantId = review.getRestaurantId();
    }
}
