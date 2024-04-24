package com.project.tableforyou.domain.menu.controller;

import com.project.tableforyou.domain.menu.dto.MenuResponseDto;
import com.project.tableforyou.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/restaurants")
@RequiredArgsConstructor
@Slf4j
public class PublicMenuController {

    private static MenuService menuService;

    /* 메뉴 불러오기. 페이징 처리 + 검색 기능 */
    @GetMapping("/{restaurantId}/menus")
    public Page<MenuResponseDto> readAll(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                         @PathVariable(name = "restaurantId") Long restaurantId,
                                         @RequestParam(required = false, value = "search-keyword") String searchKeyword) {

        if(searchKeyword == null)
            return menuService.menuPageList(restaurantId, pageable);
        else
            return menuService.menuPageSearchList(restaurantId, searchKeyword, pageable);
    }
}
