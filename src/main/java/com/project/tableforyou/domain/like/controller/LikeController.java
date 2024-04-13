package com.project.tableforyou.domain.like.controller;

import com.project.tableforyou.domain.like.service.LikeService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants/like")
public class LikeController {

    private final LikeService likeService;

    /* 가게 좋아요 증가 */
    @PostMapping("/restaurants/{restaurant_name}/like")
    public ResponseEntity<String> likeRestaurant(@PathVariable(name = "restaurant_name") String restaurant_name,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        likeService.likeRestaurant(principalDetails.getUsername(), restaurant_name);
        return ResponseEntity.ok("가게 좋아요 증가.");
    }

    /* 가게 좋아요 감소 */
    @DeleteMapping("/restaurants/{restaurant_name}/like")
    public ResponseEntity<String> unLikeRestaurant(@PathVariable(name = "restaurant_name") String restaurant_name,
                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        likeService.unLikeRestaurant(principalDetails.getUsername(), restaurant_name);
        return ResponseEntity.ok("가게 좋아요 감소.");
    }

}
