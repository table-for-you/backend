package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.restaurant.service.OwnerRestaurantService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/owner/restaurants")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final OwnerRestaurantService ownerRestaurantService;
    private final QueueReservationService queueReservationService;

    /* 가게 생성 */
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RestaurantRequestDto dto,
                                         @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ownerRestaurantService.saveRestaurant(principalDetails.getUsername(), dto);
        return ResponseEntity.ok("가게 신청이 완료 되었습니다. 승인을 기다려 주세요.");
    }

    /* 사장 가게 불러오기 */
    @GetMapping
    public List<RestaurantNameDto> readRestaurant(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ownerRestaurantService.findByRestaurantOwner(principalDetails.getUsername());
    }

    /* 가게 업데이트 */
    @PutMapping("/{restaurantId}")
    public ResponseEntity<String> update(@Valid @RequestBody RestaurantUpdateDto restaurantUpdateDto,
                                         @PathVariable(name = "restaurantId") Long restaurantId) {

        ownerRestaurantService.updateRestaurant(restaurantId, restaurantUpdateDto);
        return ResponseEntity.ok("가게 수정 완료.");
    }


    /* 가게 삭제 */
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<String> delete(@PathVariable(name = "restaurantId") Long restaurantId) {

        ownerRestaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok("가게 삭제 완료.");

    }

    /* 해당 가게 예약자 불러오기. */
    @GetMapping("/{restaurantId}/reservations")
    public List<QueueReservationResDto> readAll(@PathVariable(name = "restaurantId") Long restaurantId) {
        return queueReservationService.findAllQueueReservations(restaurantId);
    }
}
