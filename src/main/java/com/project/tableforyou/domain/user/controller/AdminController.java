package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    /* 회원 전체 불러오기, 페이징 처리 */
    @GetMapping("/users")
    public Page<UserResponseDto> readAll(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                         @RequestParam(required = false) String searchKeyword) {

        return adminService.userPageList(pageable);
    }

    /* 회원 삭제 */
    @DeleteMapping("/users/{user_id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "user_id") Long user_id) {

        adminService.deleteUser(user_id);
        return ResponseEntity.ok("회원 삭제 성공.");
    }

    /* 등록 처리 중인 가게 불러오기 */
    @GetMapping("/restaurants")
    public Page<RestaurantResponseDto> handlerRestaurant(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return adminService.handleRestaurantList(pageable);
    }

    /* 가게 추가 요청 승인*/
    @PostMapping("/restaurants/{restaurant_id}")
    public ResponseEntity<String> approvalRestaurant(@PathVariable(name = "restaurant_id") Long restaurant_id) {

        adminService.approvalRestaurant(restaurant_id);
        return ResponseEntity.ok("사용자 가게 등록 완료.");
    }

    /* 가게 삭제 */
    @DeleteMapping("/restaurants/{restaurant_id}/delete")
    public ResponseEntity<String> deleteRestaurant(@PathVariable(name = "restaurant_id") Long restaurant_id) {

        adminService.deleteRestaurant(restaurant_id);
        return ResponseEntity.ok("가게 삭제 완료.");
    }
}
