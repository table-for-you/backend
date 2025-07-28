package com.project.tableforyou.domain.reservationrefactor.controller;

import com.project.tableforyou.common.utils.api.ApiUtil;
import com.project.tableforyou.domain.reservationrefactor.service.ReservationLockManager;
import com.project.tableforyou.domain.reservationrefactor.service.queue.QueueReservationCommandService;
import com.project.tableforyou.domain.reservationrefactor.service.queue.QueueReservationQueryService;
import com.project.tableforyou.domain.reservationrefactor.service.queue.QueueReservationQueueService;
import com.project.tableforyou.domain.reservationrefactor.service.queue.ReservationQueueSseService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/restaurants")
public class QueueReservationController {
    private final ReservationLockManager reservationLockManager;
    private final QueueReservationQueryService queueReservationQueryService;
    private final QueueReservationCommandService queueReservationCommandService;
    private final ReservationQueueSseService reservationQueueSseService;
    private final QueueReservationQueueService queueReservationQueueService;

    @PostMapping("/{restaurantId}/queue-reservations")
    public ResponseEntity<?> createReservation(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(
                ApiUtil.from(
                        reservationLockManager.saveQueueReservation(
                                principalDetails.getId(),
                                principalDetails.getUsername(),
                                restaurantId
                        )
                )
        );
    }

    @GetMapping(value = "/queue/sse/{restaurantId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        queueReservationQueueService.enqueue(
                principalDetails.getId(),
                restaurantId
        );

        return reservationQueueSseService.createEmitter(
                restaurantId,
                principalDetails.getId()
        );
    }

    @DeleteMapping("/{restaurantId}/queue-reservations")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        queueReservationCommandService.cancel(
                principalDetails.getId(),
                restaurantId
        );

        return ResponseEntity.ok(ApiUtil.from("예약자 취소 성공."));
    }

    @PatchMapping("/{restaurantId}/queue-reservations")
    public ResponseEntity<?> enterReservation(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        queueReservationCommandService.markAsEntered(
                principalDetails.getId(),
                restaurantId
        );

        return ResponseEntity.ok(ApiUtil.from("예약자 입장 성공."));
    }

    @GetMapping("/{restaurantId}/queue-reservations")
    public ResponseEntity<?> getUserWaitingPosition(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(
                ApiUtil.from(
                        queueReservationQueryService.getUserWaitingPosition(
                                principalDetails.getId(),
                                restaurantId
                        )
                )
        );
    }
}
