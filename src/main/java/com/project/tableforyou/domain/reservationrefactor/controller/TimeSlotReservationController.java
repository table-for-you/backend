package com.project.tableforyou.domain.reservationrefactor.controller;

import com.project.tableforyou.common.utils.api.ApiUtil;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservationrefactor.dto.TimeSlotReservationDto;
import com.project.tableforyou.domain.reservationrefactor.service.ReservationLockManager;
import com.project.tableforyou.domain.reservationrefactor.service.timeslot.TimeSlotReservationCommandService;
import com.project.tableforyou.domain.reservationrefactor.service.timeslot.TimeSlotReservationQueryService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/restaurants")
public class TimeSlotReservationController {
    private final ReservationLockManager reservationLockManager;
    private final TimeSlotReservationQueryService timeSlotReservationQueryService;
    private final TimeSlotReservationCommandService timeSlotReservationCommandService;

    @PostMapping("/{restaurantId}/timeslot-reservations")
    public ResponseEntity<?> createReservation(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody TimeSlotReservationDto.Create createReq
    ) {
        reservationLockManager.saveTimeSlotReservation(
                principalDetails.getId(),
                principalDetails.getUsername(),
                restaurantId,
                createReq
        );

        return ResponseEntity.ok(ApiUtil.from("예약자 추가 성공."));
    }

    @DeleteMapping("/{restaurantId}/timeslot-reservations")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody TimeSlotReservationDto.Request request
    ) {
        timeSlotReservationCommandService.cancel(
                principalDetails.getId(),
                restaurantId,
                request
        );

        return ResponseEntity.ok(ApiUtil.from("예약자 취소 성공."));
    }

    @PatchMapping("/{restaurantId}/timeslot-reservations")
    public ResponseEntity<?> enterReservation(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody TimeSlotReservationDto.Request request
    ) {
        timeSlotReservationCommandService.markAsEntered(
                principalDetails.getId(),
                restaurantId,
                request
        );

        return ResponseEntity.ok(ApiUtil.from("예약자 입장 성공."));
    }

    @GetMapping("/{restaurantId}/timeslot-reservations")
    public ResponseEntity<?> getUserWaitingPosition(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(value = "date") LocalDate date,
            @RequestParam(value = "time-slot") TimeSlot timeSlot
    ) {
        return ResponseEntity.ok(
                ApiUtil.from(
                        timeSlotReservationQueryService.confirmOrCacheMyReservation(
                                principalDetails.getId(),
                                restaurantId,
                                date,
                                timeSlot
                        )
                )
        );
    }
}
