package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.restaurant.dto.PendingRestaurantDetailsDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantManageDto;
import com.project.tableforyou.domain.restaurant.service.AdminRestaurantService;
import com.project.tableforyou.domain.user.dto.UserInfoDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.service.AdminService;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.utils.api.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final AdminRestaurantService adminRestaurantService;

    /* 회원 전체 불러오기, 페이징 처리 */
    @GetMapping("/users")
    public Page<UserInfoDto> readAllUser(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                     @RequestParam(required = false, value = "type") String type,
                                     @RequestParam(required = false, value = "search-keyword") String searchKeyword,
                                     @RequestParam(required = false, value = "sort-by", defaultValue = "name") String sortBy,
                                     @RequestParam(required = false, value = "direction", defaultValue = "ASC") String direction) {

        // name이 아닌 다른 정렬 방식 선택
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (type == null)
            return adminService.readAllUser(sortedPageable);

        return switch (type) {
            case "name" -> adminService.readAllUserByName(searchKeyword, sortedPageable);
            case "nickname" -> adminService.readAllUserByNickname(searchKeyword, sortedPageable);
            case "role" ->  adminService.readAllUserByRole(searchKeyword, sortedPageable);
            default -> throw new CustomException(ErrorCode.INVALID_PARAMETER);
        };
    }

    /* 회원 정보 불러오기 */
    @GetMapping("/users/{userId}")
    public UserResponseDto readUser(@PathVariable(name = "userId") Long userId) {
        return adminService.readUserByAdmin(userId);
    }

    /* 회원 삭제 */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "userId") Long userId) {

        adminService.deleteUserByAdmin(userId);
        return ResponseEntity.ok(ApiUtil.from("회원 삭제 성공."));
    }

    /* 등록 처리 중인 가게 불러오기 */
    @GetMapping("/pending-restaurants")
    public Page<RestaurantManageDto> handlerRestaurant(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return adminRestaurantService.readPendingRestaurant(pageable);
    }

    /* 등록 처리 중인 가게 자세히 불러오기 */
    @GetMapping("/pending-restaurants/{restaurantId}")
    public PendingRestaurantDetailsDto readPendingDetailsRestaurant(@PathVariable(name = "restaurantId") Long restaurantId) {

        return adminRestaurantService.readPendingDetailsInfo(restaurantId);
    }

    /* 등록된 가게 불러오기 */
    @GetMapping("/approved-restaurants")
    public Page<RestaurantManageDto> approvedRestaurants(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, value = "type") String type,
            @RequestParam(required = false, value = "search-keyword") String searchKeyword) {

        if (type == null) {
            return adminRestaurantService.readApprovedRestaurant(pageable);
        }

        return switch (type) {
            case "restaurant" -> adminRestaurantService.readApprovedRestaurantByRestaurantName(searchKeyword, pageable);
            case "owner" -> adminRestaurantService.readApprovedRestaurantByOwnerName(searchKeyword, pageable);
            default -> throw new CustomException(ErrorCode.INVALID_PARAMETER);
        };
    }

    /* 가게 추가 요청 승인*/
    @PatchMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> approvalRestaurant(@PathVariable(name = "restaurantId") Long restaurantId) {

        adminRestaurantService.approvalRestaurant(restaurantId);
        return ResponseEntity.ok(ApiUtil.from("사용자 가게 등록 완료."));
    }

    /* 가게 삭제 (승인 거절) */
    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable(name = "restaurantId") Long restaurantId) {

        adminRestaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok(ApiUtil.from("가게 삭제 완료."));
    }
}
