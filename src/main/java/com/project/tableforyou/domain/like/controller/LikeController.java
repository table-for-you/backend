package com.project.tableforyou.domain.like.controller;

import com.project.tableforyou.domain.like.api.LikeApi;
import com.project.tableforyou.domain.like.service.LikeService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.utils.api.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class LikeController implements LikeApi {

    private final LikeService likeService;

    /* 가게 좋아요 증가 */
    @Override
    @PostMapping("{restaurantId}/like")
    public ResponseEntity<?> likeRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        likeService.likeRestaurant(principalDetails.getUsername(), restaurantId);
        return ResponseEntity.ok(ApiUtil.from("가게 좋아요 증가."));
    }

    /* 가게 좋아요 감소 */
    @Override
    @DeleteMapping("{restaurantId}/like")
    public ResponseEntity<?> unLikeRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        likeService.unLikeRestaurant(principalDetails.getUsername(), restaurantId);
        return ResponseEntity.ok(ApiUtil.from("가게 좋아요 감소."));
    }

}
