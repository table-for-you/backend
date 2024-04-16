package com.project.tableforyou.domain.reservation.dto;

import com.project.tableforyou.domain.reservation.entity.Reservation;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequestDto {

    private int booking;
    private String username;
    private String restaurant;

}
