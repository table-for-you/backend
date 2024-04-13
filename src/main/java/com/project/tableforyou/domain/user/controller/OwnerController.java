package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.user.service.OwnerService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;

    /* 가게 생성 */
    @PostMapping("/restaurants/create")
    public ResponseEntity<Object> create(@AuthenticationPrincipal PrincipalDetails principalDetails,    // 순서 조심
                                         @Valid @RequestBody RestaurantRequestDto dto,
                                         BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = ownerService.validateHandler(bindingResult);
                log.info("Failed to sign up: {}", errors);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
            }

            ownerService.save(principalDetails.getUsername(), dto);
            return ResponseEntity.ok("가게 생성 완료.");

        } catch (Exception e) {
            log.error("Error occurred during create Restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during create Restaurant");
        }
    }

    /* 가게 업데이트 */
    @PutMapping("/{restaurant}")
    public ResponseEntity<String> update(@PathVariable(name = "restaurant") String restaurant, @RequestBody RestaurantUpdateDto dto,
                                         @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ownerService.update(restaurant, principalDetails.getUsername(), dto);
        return ResponseEntity.ok("가게 수정 완료.");
    }


    /* 가게 삭제 */
    @DeleteMapping("/{restaurant}")
    public ResponseEntity<String> delete(@PathVariable(name = "restaurant") String restaurant,
                                         @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ownerService.delete(restaurant, principalDetails.getUsername());
        return ResponseEntity.ok("가게 삭제 완료.");

    }
}
