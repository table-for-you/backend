package com.project.tableforyou.domain.review.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[(권한 x) 리뷰 API]", description = "리뷰 관련 API")
public interface PublicReviewApi {

    @Operation(summary = "가게에 작성된 리뷰 불러오기", description = "가게 리뷰 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            [
                                              {
                                                "reviewId": 1,
                                                "rating": 4.5,
                                                "content": "맛있어요",
                                                "restaurantId": 1,
                                                "nickname": "테스터1",
                                                "restaurantName": "가게1"
                                              },
                                              {
                                                "reviewId": 2,
                                                "rating": 1.0,
                                                "content": "맛없어요",
                                                "restaurantId": 1
                                                "nickname": "테스터1",
                                                "restaurantName": "가게1"
                                              }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> gerReviewByRestaurantId(@PathVariable(name = "restaurantId") Long restaurantId);
}
