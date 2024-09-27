package com.project.tableforyou.domain.reservation.api;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;
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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[(권한 필요 o) 특정 시간 예약 API]", description = "권한이 필요한 특정 시간대 예약 관련 API")
public interface SecureTimeSlotReservationApi {

    @Operation(summary = "예약자 추가하기 (특정 시간) *", description = "특정 시간대에 대해 예약을 추가하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "예약자 추가 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "예약한 적 있음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "message": "이미 해당 가게에 예약을 하였습니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> saveReservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      @PathVariable(name = "restaurantId") Long restaurantId,
                                      @RequestParam(value = "date") String date,
                                      @RequestParam(value = "time-slot") TimeSlot timeSlot);

    @Operation(summary = "가게에 대해 예약을 했는지 확인하기 (특정 시간) *", description = "특정 시간대에 대해서 가게에 대해 예약을 했는지 확인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "true"
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> checkUserReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails,
                                           @RequestParam(value = "date") String date,
                                           @RequestParam(value = "time-slot") TimeSlot timeSlot);

    @Operation(summary = "예약자 삭제하기 (특정 시간) *", description = "(예약자 직접) 특정 시간에 대해 예약을 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "예약자 삭제 성공."
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
                    })),
    })
    ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @RequestParam(value = "date") String date,
                                        @RequestParam(value = "time-slot") TimeSlot timeSlot);
}
