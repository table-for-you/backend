package com.project.tableforyou.domain.review.api;

import com.project.tableforyou.domain.review.dto.ReviewDto;
import com.project.tableforyou.domain.review.dto.ReviewUpdateDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[리뷰 API]", description = "리뷰 관련 API")
public interface ReviewApi {

    @Operation(summary = "리뷰 작성하기", description = "리뷰 작성하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 작성하기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "리뷰 작성 완료."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "가게 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "restaurantNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 가게입니다."
                                        }
                                    """),
                            @ExampleObject(name = "userNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "리뷰한 적 있음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "message": "이미 해당 가게에 리뷰를 남겼습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> createReview(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                   @RequestBody ReviewDto reviewDto,
                                   @PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "가게에 작성된 리뷰 불러오기", description = "가게 리뷰 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            [
                                              {
                                                "rating": 4.5,
                                                "content": "맛있어요",
                                                "restaurantId": 1
                                              },
                                              {
                                                "rating": 1.0,
                                                "content": "맛없어요",
                                                "restaurantId": 1
                                              }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> gerReviewByRestaurantId(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "사용자가 작성한 리뷰 불러오기", description = "사용자 리뷰 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            [
                                              {
                                                "rating": 4.5,
                                                "content": "맛있어요",
                                                "restaurantId": 1
                                              },
                                              {
                                                "rating": 1.0,
                                                "content": "맛없어요",
                                                "restaurantId": 1
                                              }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> gerReviewByUserId(@PathVariable(name = "userId") Long userId);

    @Operation(summary = "리뷰 업데이트", description = "리뷰 업데이트 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 업데이트 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "리뷰 업데이트 완료."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "작성자 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "가게 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "restaurantNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 가게입니다."
                                        }
                                    """),
                            @ExampleObject(name = "reviewNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "해당 리뷰를 찾을 수 없습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> updateReview(@RequestBody ReviewUpdateDto reviewUpdateDto,
                                   @PathVariable(name = "restaurantId") Long restaurantId,
                                   @PathVariable(name = "reviewId") Long reviewId);

    @Operation(summary = "리뷰 삭제하기", description = "리뷰 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 업데이트 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "리뷰 삭제 완료."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "작성자 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "가게 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "restaurantNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 가게입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteReview(@RequestParam double rating,
                                   @PathVariable(name = "restaurantId") Long restaurantId,
                                   @PathVariable(name = "reviewId") Long reviewId);
}
