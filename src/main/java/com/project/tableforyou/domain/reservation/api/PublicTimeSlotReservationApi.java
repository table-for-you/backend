package com.project.tableforyou.domain.reservation.api;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[(권한 필요 x) 특정 시간 예약 API]", description = "권한이 필요없는 특정 시간대 예약 관련 API")
public interface PublicTimeSlotReservationApi {

    @Operation(summary = "날짜별 시간대 예약 가능한지 확인하기", description = "날짜별 시간대에 예약이 가능한지 확인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 상태 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "TWELVE_PM": 0,
                                            "SEVEN_PM": 3,
                                            "SIX_PM": 1,
                                            "NINE_AM": 2,
                                            "ONE_PM": 0,
                                            "TWO_PM": 4,
                                            "THREE_PM": 5,
                                            "FOUR_PM": 4,
                                            "EIGHT_PM": 0,
                                            "TEN_AM": 2,
                                            "FIVE_PM": 4,
                                            "ELEVEN_AM": 5
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> checkTimeReservationFull(@PathVariable(name = "restaurantId") Long restaurantId,
                                               @RequestParam(value = "date") String date);
}
