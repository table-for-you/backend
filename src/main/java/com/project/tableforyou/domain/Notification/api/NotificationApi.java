package com.project.tableforyou.domain.Notification.api;

import com.project.tableforyou.security.auth.PrincipalDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[알림 API]", description = "알림 관련 API")
public interface NotificationApi {

    @Operation(summary = "알림 목록 불러오기", description = "알림 목록을 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 목록 가져오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "content": [
                                                {
                                                    "id": 2,
                                                    "content": "가게가 승인되었습니다!",
                                                    "status": "APPROVED",
                                                    "createdTime": "2024-09-19T18:35:39.750785",
                                                    "read": false
                                                },
                                                {
                                                    "id": 1,
                                                    "content": "가게가 승인 거절되었습니다.",
                                                    "status": "REJECT",
                                                    "createdTime": "2024-09-19T18:35:30.008956",
                                                    "read": true
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
    ResponseEntity<?> readAllNotification(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                          @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "특정 알림 불러오기", description = "특정 알림을 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "content": "가게가 승인 거절되었습니다.",
                                            "status": "REJECT",
                                            "restaurantId": 35,
                                            "createdTime": "2024-09-19T18:35:30.008956"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "알림 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "해당 알림이 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> readNotification(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @PathVariable Long notificationId);
}
