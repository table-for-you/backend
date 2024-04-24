package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.reservation.dto.ReservationResponseDto;
import com.project.tableforyou.domain.reservation.service.ReservationService;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.restaurant.service.OwnerRestaurantService;
import com.project.tableforyou.handler.validate.ValidateHandler;
import com.project.tableforyou.security.auth.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/owner/restaurants")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final OwnerRestaurantService ownerRestaurantService;
    private final ValidateHandler validateHandler;
    private final ReservationService reservationService;

    /* 가게 생성 */
    @PostMapping
    public ResponseEntity<Object> create(@AuthenticationPrincipal PrincipalDetails principalDetails,    // 순서 조심
                                         @Valid @RequestBody RestaurantRequestDto dto,
                                         BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = validateHandler.validate(bindingResult);
                log.info("Failed to create Restaurant: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            ownerRestaurantService.save(principalDetails.getUsername(), dto);
            return ResponseEntity.ok("가게 신청이 완료 되었습니다. 승인을 기다려 주세요.");

        } catch (Exception e) {
            log.error("Error occurred during create Restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during create Restaurant");
        }
    }

    /* 사장 가게 불러오기 */
    @GetMapping
    public List<RestaurantNameDto> readRestaurant(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ownerRestaurantService.findByRestaurantOwner(principalDetails.getUsername());
    }

    /* 가게 업데이트 */
    @PutMapping("/{restaurantId}")
    public ResponseEntity<String> update(@PathVariable(name = "restaurantId") Long restaurantId,
                                         @RequestBody RestaurantUpdateDto dto) {

        ownerRestaurantService.update(restaurantId, dto);
        return ResponseEntity.ok("가게 수정 완료.");
    }


    /* 가게 삭제 */
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<String> delete(@PathVariable(name = "restaurantId") Long restaurantId,
                                         @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ownerRestaurantService.delete(restaurantId);
        return ResponseEntity.ok("가게 삭제 완료.");

    }

    /* 해당 가게 예약자 불러오기. */
    @GetMapping("/{restaurantId}/reservations")
    public List<ReservationResponseDto> readAll(@PathVariable(name = "restaurantId") Long restaurantId) {
        return reservationService.findAllReservation(restaurantId);
    }
}
