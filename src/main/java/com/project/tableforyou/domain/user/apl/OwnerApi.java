package com.project.tableforyou.domain.user.apl;

import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[가게주인 API]", description = "가게주인 관련 API")
public interface OwnerApi {

    @Operation(summary = "가게 생성하기 *", description = "가게 생성하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": 1
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> createRestaurant(@Valid @RequestBody RestaurantRequestDto dto,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "사장의 가게 불러오기 *", description = "사장의 가게를 모두 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        [
                                            {
                                                "id": 1,
                                                "name": "가게1"
                                            },
                                            {
                                                "id": 2,
                                                "name": "가게2"
                                                }
                                        ]
                                    """)
                    }))
    })
    ResponseEntity<?> readRestaurant(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "가게 업데이트하기 *", description = "가게 정보를 업데이트하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 업데이트 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "가게 수정 완료."
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
                    }))
    })
    ResponseEntity<?> updateRestaurant(@Valid @RequestBody RestaurantUpdateDto restaurantUpdateDto,
                                       @PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "가게 삭제하기 *", description = "가게를 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "가게 삭제 완료."
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
                    }))
    })
    ResponseEntity<?> deleteRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "해당 가게 예약자 불러오기 (번호표) *", description = "번호표에 대한 가게 예약자를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 예약자 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        [
                                            {
                                                "booking": 1,
                                                "username": "test1"
                                            },
                                            {
                                                "booking": 2,
                                                "username": "test2"
                                                }
                                        ]
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "해당 가게 사장 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> readAllRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "예약 순서 미루기 (번호표) *", description = "(가게 사장 직접) 번호표에 대한 예약 순서 미루기 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 미루기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "예약자 미루기 + 앞당기기 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "잘못된 값 전달(예약자 보다 크거나 현재 번호보다 작은 경우)",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 400,
                                            "message": "올바른 값을 입력해주세요."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "해당 가게 사장 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "해당 예약번호 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "해당하는 예약번호가 없습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> postponedGuestBooking(@PathVariable(name = "restaurantId") Long restaurantId,
                                            @PathVariable(name = "username") String username,
                                            @RequestBody QueueReservationReqDto reservationDto,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "예약자 삭제 (번호표) *", description = "(가게 사장 직접) 번호표에 대한 예약 삭제 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약자 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "예약자 삭제 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "해당 가게 사장 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                        @PathVariable(name = "username") String username,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "해당 가게 예약자 불러오기 (특정 시간) *", description = "특정시간에 대한 가게 예약자를 불러오는 API입니다." +
                                                        "<br> 시간대는 NINE_AM, TEN_AM ~ SEVEN_PM, EIGHT_PM 까지 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 예약자 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        [
                                            {
                                                "username": "test1"
                                            },
                                            {
                                                "username": "test2"
                                                }
                                        ]
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "해당 가게 사장 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> readAllTimeSlotReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                 @RequestParam(value = "time-slot") TimeSlot timeSlot,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "예약 삭제하기 (특정 시간) *", description = "특정시간에 대한 가게 예약자를 삭제하는  API입니다." +
                                                        "<br> 시간대는 NINE_AM, TEN_AM ~ SEVEN_PM, EIGHT_PM 까지 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약자 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "예약자 삭제 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "해당 가게 사장 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                        @PathVariable(name = "username") String username,
                                        @RequestParam(value = "time-slot") TimeSlot timeSlot,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails);
}
