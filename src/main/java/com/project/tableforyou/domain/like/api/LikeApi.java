package com.project.tableforyou.domain.like.api;

import com.project.tableforyou.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[좋아요 API]", description = "좋아요 관련 API")
public interface LikeApi {

    @Operation(summary = "가게 좋아요하기 *", description = "가게를 좋아요하는 API입니다.")
    ResponseEntity<?> likeRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "가게 좋아요 취소하기 *", description = "가게에 대해 좋아요를 취소하는 API입니다.")
    ResponseEntity<?> unLikeRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails);
}
