package com.project.tableforyou.domain.review.dto;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.review.entity.Review;
import com.project.tableforyou.domain.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewDto {

    @Min(0) @Max(5)
    private double rating;

    private String content;

    public Review toEntity(User user, Restaurant restaurant) {
        return Review.builder()
                .rating(rating)
                .content(content)
                .restaurantId(restaurant.getId())
                .user(user)
                .restaurant(restaurant)
                .build();
    }
}
