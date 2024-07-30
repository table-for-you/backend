package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.api.PublicTimeSlotReservationApi;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.service.TimeSlotReservationService;
import com.project.tableforyou.utils.api.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/restaurants")
public class PublicTimeSlotReservationController implements PublicTimeSlotReservationApi {

    private final TimeSlotReservationService timeSlotReservationService;

    /* 특정 시간대 예약 다 찼는지 확인 */
    @Override
    @GetMapping("/{restaurantId}/timeslot-reservations-full-check")
    public ResponseEntity<?> checkTimeReservationFull(@PathVariable(name = "restaurantId") Long restaurantId,
                                                            @RequestParam(value = "time-slot") TimeSlot timeSlot) {

        return ResponseEntity.ok(ApiUtil.from(timeSlotReservationService.checkTimeSlotReservationFull(restaurantId, timeSlot)));
    }
}
