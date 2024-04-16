package com.project.tableforyou.domain.like.controller;

import com.project.tableforyou.domain.like.service.LikeService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class LikeController {

    private final LikeService likeService;

    /* 가게 좋아요 증가 */
    @PostMapping("{restaurantId}/like")
    public ResponseEntity<String> likeRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        likeService.likeRestaurant(principalDetails.getUsername(), restaurantId);
        return ResponseEntity.ok("가게 좋아요 증가.");
    }

    /* 가게 좋아요 감소 */
    @DeleteMapping("{restaurantId}/like")
    public ResponseEntity<String> unLikeRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        likeService.unLikeRestaurant(principalDetails.getUsername(), restaurantId);
        return ResponseEntity.ok("가게 좋아요 감소.");
    }

}
