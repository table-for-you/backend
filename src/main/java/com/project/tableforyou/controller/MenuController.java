package com.project.tableforyou.controller;

import com.project.tableforyou.domain.dto.MenuDto;
import com.project.tableforyou.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;

    /* 메뉴 생성 */
    @PostMapping("/{restaurant}/menu/create")
    public ResponseEntity<String> create(@PathVariable(name = "restaurant") String restaurant, @RequestBody MenuDto.Request dto) {

        menuService.save(restaurant, dto);
        return ResponseEntity.ok("메뉴 생성 완료.");
    }

    /* 메뉴 불러오기. 페이징 처리 + 검색 기능 */
    @GetMapping("/{restaurant}/menu")
    public Page<MenuDto.Response> readAll(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                          @PathVariable(name = "restaurant") String restaurant,
                                          @RequestParam(required = false) String searchKeyword) {

        if(searchKeyword == null)
            return menuService.menuPageList(restaurant, pageable);
        else
            return menuService.menuPageSearchList(restaurant, searchKeyword, pageable);
    }

    /* 메뉴 업데이트 */
    @PutMapping("/{restaurant}/menu/{menu_id}")
    public ResponseEntity<String> update(@PathVariable(name = "restaurant") String restaurant,
                                         @PathVariable(name = "menu_id") Long menu_id, @RequestBody MenuDto.Request dto) {

        menuService.update(restaurant, menu_id, dto);
        return ResponseEntity.ok("메뉴 업데이트 완료.");
    }

    /* 메뉴 삭제 */
    @DeleteMapping("/{restaurant}/menu/{menu_id}")
    public ResponseEntity<String> delete(@PathVariable(name = "restaurant") String restaurant,
                                         @PathVariable(name = "menu_id") Long menu_id) {

        menuService.delete(restaurant, menu_id);
        return ResponseEntity.ok("메뉴 삭제 완료.");
    }
}
