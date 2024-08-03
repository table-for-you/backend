package com.project.tableforyou.domain.user.apl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
                                                            "<br>type에 따라 nickname, role을 기준으로 검색을 할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 리스트 가져오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "no-search", value = """
                                        {
                                            "content": [
                                                {
                                                    "id": 1,
                                                    "nickname": "닉네임1",
                                                    "role": "USER"
                                                },
                                                {
                                                    "id": 2,
                                                    "nickname": "닉네임2",
                                                    "role": "USER"
                                                }
                                            ],
                                            "pageable": {
                                                "sort": {
                                                    "sorted": true,
                                                    "unsorted": false,
                                                    "empty": false
                                                },
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 10,
                                                "paged": true,
                                                "unpaged": false
                                            },
                                            "last": false,
                                            "totalPages": 5,
                                            "totalElements": 100,
                                            "size": 10,
                                            "number": 0,
                                            "sort": {
                                            "sorted": true,
                                            "unsorted": false,
                                            "empty": false
                                            },
                                            "first": true,
                                            "numberOfElements": 10,
                                            "empty": false
                                        }
                                    """),
                            @ExampleObject(name = "search", value = """
                                        {
                                            "searchKeyword": "홍",
                                            "content": [
                                                {
                                                    "id": 3,
                                                    "nickname": "홍길동1",
                                                    "role": "USER"
                                                },
                                                {
                                                    "id": 4,
                                                    "nickname": "홍길동2",
                                                    "role": "USER"
                                                }
                                            ],
                                            "pageable": {
                                                "sort": {
                                                    "sorted": true,
                                                    "unsorted": false,
                                                    "empty": false
                                                },
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 10,
                                                "paged": true,
                                                "unpaged": false
                                            },
                                            "last": true,
                                            "totalPages": 1,
                                            "totalElements": 2,
                                            "size": 10,
                                            "number": 0,
                                            "sort": {
                                                "sorted": true,
                                                "unsorted": false,
                                                "empty": false
                                            },
                                            "first": true,
                                            "numberOfElements": 2,
                                            "empty": false
                                        }
                                    """),
                    })),
            @ApiResponse(responseCode = "400", description = "잘못된 type 입력",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 400,
                                            "message": "올바른 값을 입력해주세요."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> readAllUser(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                  @RequestParam(required = false, value = "type") String type,
                                  @RequestParam(required = false, value = "search-keyword") String searchKeyword,
                                  @RequestParam(required = false, value = "sort-by", defaultValue = "id") String sortBy,
                                  @RequestParam(required = false, value = "direction", defaultValue = "ASC") String direction);

    @Operation(summary = "회원 정보 불러오기 *", description = "특정 회원의 정보를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "id": 1,
                                            "username": "test",
                                            "email": "test@naver.com",
                                            "nickname": "테스터",
                                            "age": "20",
                                            "role": "USER",
                                            "createdTime": "2023-03-05T12:00:00",
                                            "modifiedTime": "2023-04-05T12:00:00"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> readUser(@PathVariable(name = "userId") Long userId);

    @Operation(summary = "회원 삭제하기 *", description = "회원을 서비스로부터 탈퇴시키는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "회원 삭제 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> deleteUser(@PathVariable(name = "userId") Long userId);

    @Operation(summary = "등록 처리 중인 가게 불러오기 *", description = "등록 처리 중인 가게를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 처리중인 가게 리스트 가져오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "content": [
                                                {
                                                    "id": 1,
                                                    "name": "가게1",
                                                    "ownerName": "가게사장1"
                                                },
                                                {
                                                    "id": 2,
                                                    "name": "가게2",
                                                    "ownerName": "가게사장2"
                                                }
                                            ],
                                            "pageable": {
                                                "sort": {
                                                    "sorted": true,
                                                    "unsorted": false,
                                                    "empty": false
                                                },
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 20,
                                                "paged": true,
                                                "unpaged": false
                                            },
                                            "last": false,
                                            "totalPages": 5,
                                            "totalElements": 100,
                                            "size": 20,
                                            "number": 0,
                                            "sort": {
                                            "sorted": true,
                                            "unsorted": false,
                                            "empty": false
                                            },
                                            "first": true,
                                            "numberOfElements": 20,
                                            "empty": false
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> handlerRestaurant(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable);

    @Operation(summary = "등록 처리 중인 가게 자세히 불러오기 *", description = "등록 처리 중인 가게 자세히 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "id": 1,
                                            "time": "09:00-22:00",
                                            "name": "가게이름",
                                            "region": "SEOUL",
                                            "location": "가게 위치",
                                            "tel": "02-456-7890",
                                            "description": "가게 설명",
                                            "restaurantImage": "http://example.com/image1.jpg",
                                            "businessLicenseImage": "http://example.com/image2.jpg",
                                            "foodType": "양식"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "가게 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 가게입니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> readPendingDetailsRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "등록된 가게 불러오기 *", description = "등록된 가게를 불러오는 API입니다." +
                                                            "검색 type으로는 restaurant, owner이 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 처리중인 가게 리스트 가져오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "no-search", value = """
                                        {
                                            "content": [
                                                {
                                                    "id": 1,
                                                    "name": "가게1",
                                                    "ownerName": "가게사장1"
                                                },
                                                {
                                                    "id": 2,
                                                    "name": "가게2",
                                                    "ownerName": "가게사장2"
                                                }
                                            ],
                                            "pageable": {
                                                "sort": {
                                                    "sorted": true,
                                                    "unsorted": false,
                                                    "empty": false
                                                },
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 20,
                                                "paged": true,
                                                "unpaged": false
                                            },
                                            "last": false,
                                            "totalPages": 5,
                                            "totalElements": 100,
                                            "size": 20,
                                            "number": 0,
                                            "sort": {
                                            "sorted": true,
                                            "unsorted": false,
                                            "empty": false
                                            },
                                            "first": true,
                                            "numberOfElements": 20,
                                            "empty": false
                                        }
                                    """),
                            @ExampleObject(name = "search", value = """
                                        {
                                            "searchKeyword": "가게",
                                            "content": [
                                                {
                                                    "id": 1,
                                                    "name": "가게1",
                                                    "ownerName": "가게사장1"
                                                },
                                                {
                                                    "id": 2,
                                                    "name": "가게2",
                                                    "ownerName": "가게사장2"
                                                }
                                            ],
                                            "pageable": {
                                                "sort": {
                                                    "sorted": true,
                                                    "unsorted": false,
                                                    "empty": false
                                                },
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 20,
                                                "paged": true,
                                                "unpaged": false
                                            },
                                            "last": true,
                                            "totalPages": 1,
                                            "totalElements": 2,
                                            "size": 20,
                                            "number": 0,
                                            "sort": {
                                                "sorted": true,
                                                "unsorted": false,
                                                "empty": false
                                            },
                                            "first": true,
                                            "numberOfElements": 2,
                                            "empty": false
                                        }
                                    """),
                    }))
    })
    ResponseEntity<?> approvedRestaurants(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, value = "type") String type,
            @RequestParam(required = false, value = "search-keyword") String searchKeyword);

    @Operation(summary = "가게 추가 요청 승인하기 *", description = "가게 추가 요청을 승인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 추가 요청 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "사용자 가게 등록 완료."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "가게 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 가게입니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> approvalRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "가게 삭제 (승인 거절) *", description = "가게 삭제 및 가게 추가 요청을 거절하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 추가 요청 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "사용자 가게 등록 완료."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "가게 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "가게 삭제 완료."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> deleteRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);
}
