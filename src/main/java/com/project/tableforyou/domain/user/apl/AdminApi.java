package com.project.tableforyou.domain.user.apl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[관리자 API]", description = "관리자 관련 API")
public interface AdminApi {

    @Operation(summary = "회원 전체 불러오기 + 검색 *", description = "전체 회원을 불러오는 API입니다." +
                                                            "<br>type에 따라 닉네임, 권한을 기준으로 검색을 할 수 있습니다.")
    ResponseEntity<?> readAllUser(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                  @RequestParam(required = false, value = "type") String type,
                                  @RequestParam(required = false, value = "search-keyword") String searchKeyword,
                                  @RequestParam(required = false, value = "sort-by", defaultValue = "id") String sortBy,
                                  @RequestParam(required = false, value = "direction", defaultValue = "ASC") String direction);

    @Operation(summary = "회원 정보 불러오기 *", description = "특정 회원의 정보를 불러오는 API입니다.")
    ResponseEntity<?> readUser(@PathVariable(name = "userId") Long userId);

    @Operation(summary = "회원 삭제하기 *", description = "회원을 서비스로부터 탈퇴시키는 API입니다.")
    ResponseEntity<?> deleteUser(@PathVariable(name = "userId") Long userId);

    @Operation(summary = "등록 처리 중인 가게 불러오기 *", description = "등록 처리 중인 가게를 불러오는 API입니다.")
    ResponseEntity<?> handlerRestaurant(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable);

    @Operation(summary = "등록 처리 중인 가게 자세히 불러오기 *", description = "등록 처리 중인 가게 자세히 불러오는 API입니다.")
    ResponseEntity<?> readPendingDetailsRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "등록된 가게 불러오기 *", description = "등록된 가게를 불러오는 API입니다.")
    ResponseEntity<?> approvedRestaurants(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, value = "type") String type,
            @RequestParam(required = false, value = "search-keyword") String searchKeyword);

    @Operation(summary = "가게 추가 요청 승인하기 *", description = "가게 추가 요청을 승인하는 API입니다.")
    ResponseEntity<?> approvalRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "가게 삭제 (승인 거절) *", description = "가게 삭제 및 가게 추가 요청을 거절하는 API입니다.")
    ResponseEntity<?> deleteRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);
}
