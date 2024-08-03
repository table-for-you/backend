package com.project.tableforyou.domain.like.api;

import com.project.tableforyou.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[좋아요 API]", description = "좋아요 관련 API")
public interface LikeApi {

    @Operation(summary = "가게 좋아요하기 *", description = "가게를 좋아요하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 좋아요 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "가게 좋아요 증가."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 또는 가게 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "UserNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """),
                            @ExampleObject(name = "StoreNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 가게입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 해당 가게에 좋아요했음.",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "message": "이미 해당 가게를 좋아요 하였습니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> likeRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "가게 좋아요 취소하기 *", description = "가게에 대해 좋아요를 취소하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 좋아요 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "가게 좋아요 증가."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 또는 가게 또는 좋아요 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "UserNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """),
                            @ExampleObject(name = "StoreNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 가게입니다."
                                        }
                                    """),
                            @ExampleObject(name = "LikeNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "해당 가게에 좋아요를 누른 적이 없습니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> unLikeRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails);
}
