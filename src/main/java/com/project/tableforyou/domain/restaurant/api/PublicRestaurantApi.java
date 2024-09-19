package com.project.tableforyou.domain.restaurant.api;

import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Tag(name = "[(권한 필요 x) 가게 API]", description = "권한이 필요없는 가게 관련 API")
public interface PublicRestaurantApi {

    @Operation(summary = "특정 가게 불러오기", description = "특정 가게를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "id": 1,
                                            "ownerId": 1,
                                            "usedSeats": 20,
                                            "totalSeats": 50,
                                            "rating": 4.5,
                                            "time": "09:00-22:00",
                                            "name": "가게이름",
                                            "region": "SEOUL",
                                            "location": "가게 위치",
                                            "tel": "02-456-7890",
                                            "description": "가게 설명",
                                            "restaurantImage": "http://example.com/image.jpg",
                                            "foodType": "KOREAN",
                                            "likeCount": 150
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "가게 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 가게입니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> readRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "전체 가게 불러오기", description = "전체 가게를 불러오는 API입니다." +
          "<br>검색 type으로는 restaurant(가게 이름), region(지역), location(주소), food(음식 타입)가 있습니다." +
            "<br>또한, sortBy는 어떤 것으로 정렬을 할 것인지(name, id, rating 등), direction는 내림차순(DESC), 오름차순(ASC)가 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 리스트 가져오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "no-search", value = """
                                        {
                                            "content": [
                                                {
                                                    "id": 1,
                                                    "name": "가게1",
                                                    "rating": 4.5,
                                                    "ratingNum": 150,
                                                    "restaurantImage": "http://example.com/image1.jpg",
                                                    "foodType": "KOREAN"
                                                },
                                                {
                                                    "id": 2,
                                                    "name": "가게2",
                                                    "rating": 4.7,
                                                    "ratingNum": 200,
                                                    "restaurantImage": "http://example.com/image2.jpg",
                                                    "foodType": "KOREAN"
                                                }
                                            ],
                                            "pageable": {
                                                "sort": {
                                                    "sorted": true,
                                                    "unsorted": false,
                                                    "empty": false
                                                },
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 20,
                                                "paged": true,
                                                "unpaged": false
                                            },
                                            "last": false,
                                            "totalPages": 5,
                                            "totalElements": 100,
                                            "size": 20,
                                            "number": 0,
                                            "sort": {
                                            "sorted": true,
                                            "unsorted": false,
                                            "empty": false
                                            },
                                            "first": true,
                                            "numberOfElements": 20,
                                            "empty": false
                                        }
                                    """),
                            @ExampleObject(name = "search", value = """
                                        {
                                            "searchKeyword": "버거",
                                            "content": [
                                                {
                                                    "id": 3,
                                                    "name": "핫버거",
                                                    "rating": 4.2,
                                                    "ratingNum": 90,
                                                    "restaurantImage": "http://example.com/image3.jpg",
                                                    "foodType": "KOREAN"
                                                },
                                                {
                                                    "id": 4,
                                                    "name": "쿨버거",
                                                    "rating": 4.0,
                                                    "ratingNum": 85,
                                                    "restaurantImage": "http://example.com/image4.jpg",
                                                    "foodType": "KOREAN"
                                                }
                                            ],
                                            "pageable": {
                                                "sort": {
                                                    "sorted": true,
                                                    "unsorted": false,
                                                    "empty": false
                                                },
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 20,
                                                "paged": true,
                                                "unpaged": false
                                            },
                                            "last": true,
                                            "totalPages": 1,
                                            "totalElements": 2,
                                            "size": 20,
                                            "number": 0,
                                            "sort": {
                                                "sorted": true,
                                                "unsorted": false,
                                                "empty": false
                                            },
                                            "first": true,
                                            "numberOfElements": 2,
                                            "empty": false
                                        }
                                    """),
                    })),
            @ApiResponse(responseCode = "400", description = "잘못된 type 입력",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 400,
                                            "message": "올바른 값을 입력해주세요."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> readAllRestaurant(
            @PageableDefault(size = 20, sort = "rating", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false, value = "type") String type,
            @RequestParam(required = false, value = "search-keyword") String searchKeyword,
            @RequestParam(required = false, value = "sort-by", defaultValue = "rating") String sortBy,
            @RequestParam(required = false, value = "direction", defaultValue = "DESC") String direction);

    @Operation(summary = "현재 가게 예약자 수 불러오기 (번호표)", description = "번호표에 대한 현재 가게의 예약자 수를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약자 수 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": 10
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> waiting(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "좌석 업데이트", description = "가게 좌석을 업데이트 API입니다." +
                                        "<br>좌석 증가시 increase는 true, 감소시 increase는 false." +
    "이 외는 서버에서 처리합니다. (가게 좌석 증가 + 가게 다 찼을 시, 번호표 예약으로 자동 이동, 가게 좌석 감소 + 가게 다 찼을 시, 다음 예약 순번 자동으로 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좌석 업데이트 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "increaseSeats", value = """
                                        {
                                            "response": "가게 좌석 증가 완료."
                                        }
                                    """),
                            @ExampleObject(name = "decreaseSeats", value = """
                                        {
                                            "response": "가게 좌석 감소 완료."
                                        }username + "님 입장"
                                    """),
                            @ExampleObject(name = "decreaseAndExistsReservation", value = """
                                        {
                                            "response": "test님 입장"
                                        }
                                    """)
                    })),

    })
    void updateFullUsedSeats(@PathVariable(name = "restaurantId") Long restaurantId,
                             @RequestParam("increase") boolean increase,
                             HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    @Operation(summary = "좌석 업데이트 (실제 호출 x)", description = "좌석 업데이트를 하는 API입니다." +
    "<br>실제 호출하지 않고, 좌석 업데이트(/public/restaurants/{restaurantId}/update-used-seats)에 의해 자동 호출됩니다.")
    ResponseEntity<?> updateUsedSeats(@PathVariable(name = "restaurantId") Long restaurantId,
                                      @PathVariable(name = "value") int value);
}
