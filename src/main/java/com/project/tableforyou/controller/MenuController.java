package com.project.tableforyou.controller;

import com.project.tableforyou.domain.dto.MenuDto;
import com.project.tableforyou.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /* 메뉴 생성 */
    @PostMapping("/{restaurant_id}/menu/create")
    public ResponseEntity<String> create(@PathVariable Long restaurant_id, @RequestBody MenuDto.Request dto) {
        try {
            menuService.save(restaurant_id, dto);
            return ResponseEntity.ok("메뉴 생성 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메뉴 생성 실패.");
        }
    }

    /* 메뉴 불러오기. 페이징 처리 + 검색 기능 */
    @GetMapping("/{restaurant_id}/menu")
    public Page<MenuDto.Response> readAll(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                          @PathVariable Long restaurant_id, @RequestParam(required = false) String searchKeyword) {

        if(searchKeyword == null)
            return menuService.menuPageList(restaurant_id, pageable);
        else
            return menuService.menuPageSearchList(restaurant_id, searchKeyword, pageable);
    }

    /* 메뉴 업데이트 */
    @PutMapping("/menu/{menu_id}")
    public ResponseEntity<String> update(@PathVariable Long menu_id, @RequestBody MenuDto.Request dto) {
        try {
            menuService.update(menu_id, dto);
            return ResponseEntity.ok("메뉴 업데이트 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메뉴 업데이트 실패.");
        }
    }

    /* 메뉴 삭제 */
    @DeleteMapping("/menu/{menu_id}")
    public ResponseEntity<String> delete(@PathVariable Long menu_id) {
        try {
            menuService.delete(menu_id);
            return ResponseEntity.ok("메뉴 삭제 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메뉴 삭제 실패.");
        }
    }
}
