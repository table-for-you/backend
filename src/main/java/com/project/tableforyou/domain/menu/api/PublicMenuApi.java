package com.project.tableforyou.domain.menu.api;

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

@Tag(name = "[(권한 필요 x) 메뉴 API]", description = "권한이 필요없는 메뉴 관련 API")
public interface PublicMenuApi {

    @Operation(summary = "메뉴 불러오기", description = "메뉴 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 리스트 가져오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "no-search", value = """
                                        {
                                            "content": [
                                                {
                                                    "id": 1,
                                                    "name": "스파게티",
                                                    "price": "10,000",
                                                    "createdTime": "2023-01-01T12:00:00",
                                                    "modifiedTime": "2023-02-01T12:00:00"
                                                },
                                                {
                                                    "id": 2,
                                                    "name": "피자",
                                                    "price": "15,000",
                                                    "createdTime": "2023-01-10T12:00:00",
                                                    "modifiedTime": "2023-02-10T12:00:00"
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
                                            "searchKeyword": "버거",
                                            "content": [
                                                {
                                                    "id": 3,
                                                    "name": "불고기버거",
                                                    "price": "6,000",
                                                    "createdTime": "2023-03-01T12:00:00",
                                                    "modifiedTime": "2023-04-01T12:00:00"
                                                },
                                                {
                                                    "id": 4,
                                                    "name": "치킨버거",
                                                    "price": "7,000",
                                                    "createdTime": "2023-03-05T12:00:00",
                                                    "modifiedTime": "2023-04-05T12:00:00"
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
                    })
            )
    })
    ResponseEntity<?> readAllMenu(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                  @PathVariable(name = "restaurantId") Long restaurantId,
                                  @RequestParam(required = false, value = "search-keyword") String searchKeyword);
}
