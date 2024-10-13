package com.project.tableforyou.domain.review.controller;

import com.project.tableforyou.common.utils.api.ApiUtil;
import com.project.tableforyou.domain.review.dto.ReviewDto;
import com.project.tableforyou.domain.review.dto.ReviewUpdateDto;
import com.project.tableforyou.domain.review.service.ReviewService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /* 리뷰 생성 */
    @PostMapping("/restaurants/{restaurantId}/reviews")
    public ResponseEntity<?> createReview(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                          @RequestBody ReviewDto reviewDto,
                                          @PathVariable(name = "restaurantId") Long restaurantId) {

        reviewService.createReview(principalDetails.getId(), restaurantId, reviewDto);
        return ResponseEntity.ok(ApiUtil.from("리뷰 작성 완료."));
    }

    /* 가게 리뷰 불러오기 */
    @GetMapping("/restaurants/{restaurantId}/reviews")
    public ResponseEntity<?> gerReviewByRestaurantId(@PathVariable(name = "restaurantId") Long restaurantId) {

        return ResponseEntity.ok(reviewService.getReviewByRestaurantId(restaurantId));
    }

    /* 사용자가 작성한 리뷰 불러오기 */
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<?> gerReviewByUserId(@PathVariable(name = "userId") Long userId) {

        return ResponseEntity.ok(reviewService.getReviewByUserId(userId));
    }

    /* 리뷰 업데이트 */
    @PutMapping("/restaurants/{restaurantId}/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(@RequestBody ReviewUpdateDto reviewUpdateDto,
                                          @PathVariable(name = "restaurantId") Long restaurantId,
                                          @PathVariable(name = "reviewId") Long reviewId) {

        reviewService.updateReview(restaurantId, reviewId, reviewUpdateDto);
        return ResponseEntity.ok(ApiUtil.from("리뷰 업데이트 완료."));
    }

    /* 리뷰 삭제 */
    @DeleteMapping("/restaurants/{restaurantId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@RequestParam double rating,
                                          @PathVariable(name = "restaurantId") Long restaurantId,
                                          @PathVariable(name = "reviewId") Long reviewId) {

        reviewService.deleteReview(restaurantId, reviewId, rating);
        return ResponseEntity.ok(ApiUtil.from("리뷰 삭제 완료."));
    }
}
