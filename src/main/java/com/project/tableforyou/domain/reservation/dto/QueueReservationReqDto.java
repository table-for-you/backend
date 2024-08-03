package com.project.tableforyou.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(name = "QueueReservationReqDto", description = "예약 생성 요청 DTO")
public class QueueReservationReqDto {

    @Schema(description = "예약순서 미루기 번호", example = "8")
    private int booking;
}
