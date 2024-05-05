package com.project.tableforyou.domain.reservation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReservationRequestDto {

    private int booking;
    private String username;
    private String restaurant;

}
