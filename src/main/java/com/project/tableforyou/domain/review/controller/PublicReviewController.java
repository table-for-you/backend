package com.project.tableforyou.domain.review.controller;

import com.project.tableforyou.domain.review.api.PublicReviewApi;
import com.project.tableforyou.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class PublicReviewController implements PublicReviewApi {

    private final ReviewService reviewService;

    /* 가게 리뷰 불러오기 */
    @Override
    @GetMapping("/restaurants/{restaurantId}/reviews")
    public ResponseEntity<?> gerReviewByRestaurantId(@PathVariable(name = "restaurantId") Long restaurantId) {

        return ResponseEntity.ok(reviewService.getReviewByRestaurantId(restaurantId));
    }
}
