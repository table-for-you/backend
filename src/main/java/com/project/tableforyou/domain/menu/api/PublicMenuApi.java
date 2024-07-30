package com.project.tableforyou.domain.menu.api;

import io.swagger.v3.oas.annotations.Operation;
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
    ResponseEntity<?> readAllMenu(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                  @PathVariable(name = "restaurantId") Long restaurantId,
                                  @RequestParam(required = false, value = "search-keyword") String searchKeyword);
}
