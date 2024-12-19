package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.common.utils.api.ApiUtil;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.restaurant.service.AdminRestaurantService;
import com.project.tableforyou.domain.user.apl.AdminApi;
import com.project.tableforyou.domain.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController implements AdminApi {

    private final AdminService adminService;
    private final AdminRestaurantService adminRestaurantService;

    /* 회원 전체 불러오기, 페이징 처리 */
    @Override
    @GetMapping("/users")
    public ResponseEntity<?> readAllUser(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                     @RequestParam(required = false, value = "type") String type,
                                     @RequestParam(required = false, value = "search-keyword") String searchKeyword,
                                     @RequestParam(required = false, value = "sort-by", defaultValue = "id") String sortBy,
                                     @RequestParam(required = false, value = "direction", defaultValue = "ASC") String direction) {

        // name이 아닌 다른 정렬 방식 선택
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (type == null)
            return ResponseEntity.ok(adminService.readAllUser(sortedPageable));

        return switch (type) {
            case "nickname" -> ResponseEntity.ok(adminService.readAllUserByNickname(searchKeyword, sortedPageable));
            case "role" ->  ResponseEntity.ok(adminService.readAllUserByRole(searchKeyword, sortedPageable));
            default -> throw new CustomException(ErrorCode.INVALID_PARAMETER);
        };
    }

    /* 회원 정보 불러오기 */
    @Override
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> readUser(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(adminService.readUserByAdmin(userId));
    }

    /* 회원 삭제 */
    @Override
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "userId") Long userId) {

        adminService.deleteUserByAdmin(userId);
        return ResponseEntity.ok(ApiUtil.from("회원 삭제 성공."));
    }

    /* 등록 처리 중인 가게 불러오기 */
    @Override
    @GetMapping("/pending-restaurants")
    public ResponseEntity<?> handlerRestaurant(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(adminRestaurantService.readPendingRestaurant(pageable));
    }

    /* 등록 처리 중인 가게 자세히 불러오기 */
    @Override
    @GetMapping("/pending-restaurants/{restaurantId}")
    public ResponseEntity<?> readPendingDetailsRestaurant(@PathVariable(name = "restaurantId") Long restaurantId) {

        return ResponseEntity.ok(adminRestaurantService.readPendingDetailsInfo(restaurantId));
    }

    /* 등록된 가게 불러오기 */
    @Override
    @GetMapping("/approved-restaurants")
    public ResponseEntity<?> approvedRestaurants(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, value = "type") String type,
            @RequestParam(required = false, value = "search-keyword") String searchKeyword) {

        if (type == null) {
            return ResponseEntity.ok(adminRestaurantService.readApprovedRestaurant(pageable));
        }

        return switch (type) {
            case "restaurant" -> ResponseEntity.ok(adminRestaurantService.readApprovedRestaurantByRestaurantName(searchKeyword, pageable));
            case "owner" -> ResponseEntity.ok(adminRestaurantService.readApprovedRestaurantByOwnerName(searchKeyword, pageable));
            default -> throw new CustomException(ErrorCode.INVALID_PARAMETER);
        };
    }

    /* 가게 상태 변경 (승인, 거절) */
    @Override
    @PatchMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> updateRestaurantStatus(@PathVariable(name = "restaurantId") Long restaurantId,
                                                    @RequestParam(value = "status") RestaurantStatus status) {

        adminRestaurantService.updateRestaurantStatus(restaurantId, status);
        return ResponseEntity.ok(ApiUtil.from("가게 상태 변경 완료."));
    }

    /* 가게 삭제  */
    @Override
    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable(name = "restaurantId") Long restaurantId) {

        adminRestaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok(ApiUtil.from("가게 삭제 완료."));
    }
}
