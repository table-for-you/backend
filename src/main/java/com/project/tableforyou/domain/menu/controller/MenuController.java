package com.project.tableforyou.domain.menu.controller;

import com.project.tableforyou.domain.menu.dto.MenuRequestDto;
import com.project.tableforyou.domain.menu.dto.MenuResponseDto;
import com.project.tableforyou.domain.menu.dto.MenuUpdateDto;
import com.project.tableforyou.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;

    /* 메뉴 생성 */
    @PostMapping("/{restaurantId}/menus")
    public ResponseEntity<String> create(@PathVariable(name = "restaurantId") Long restaurantId, @RequestBody MenuRequestDto dto) {

        menuService.save(restaurantId, dto);
        return ResponseEntity.ok("메뉴 생성 완료.");
    }

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

    /* 메뉴 업데이트 */
    @PutMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<String> update(@PathVariable(name = "restaurantId") Long restaurantId,
                                         @PathVariable(name = "menuId") Long menuId, @RequestBody MenuUpdateDto dto) {

        menuService.update(restaurantId, menuId, dto);
        return ResponseEntity.ok("메뉴 업데이트 완료.");
    }

    /* 메뉴 삭제 */
    @DeleteMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<String> delete(@PathVariable(name = "restaurantId") Long restaurantId,
                                         @PathVariable(name = "menuId") Long menuId) {

        menuService.delete(restaurantId, menuId);
        return ResponseEntity.ok("메뉴 삭제 완료.");
    }
}
