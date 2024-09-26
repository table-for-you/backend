package com.project.tableforyou.domain.menu.controller;

import com.project.tableforyou.domain.menu.api.SecureMenuApi;
import com.project.tableforyou.domain.menu.dto.MenuRequestDto;
import com.project.tableforyou.domain.menu.dto.MenuUpdateDto;
import com.project.tableforyou.domain.menu.service.MenuService;
import com.project.tableforyou.utils.api.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Slf4j
public class SecureMenuController implements SecureMenuApi {

    private final MenuService menuService;

    /* 메뉴 생성 */
    @Override
    @PostMapping("/{restaurantId}/menus")
    public ResponseEntity<?> createMenu(@Valid @RequestPart(value = "menuDto") MenuRequestDto menuDto,
                                        @PathVariable(name = "restaurantId") Long restaurantId,
                                        @RequestPart(value = "menuImage", required = false) MultipartFile menuImage) {

        return ResponseEntity.ok(ApiUtil.from(menuService.saveMenu(restaurantId, menuDto, menuImage)));
    }

    /* 메뉴 이미지 업데이트 */
    @Override
    @PatchMapping("/{restaurantId}/menus/{menuId}/menu-image")
    public ResponseEntity<?> updateMenuImage(@PathVariable(name = "restaurantId") Long restaurantId,
                                             @PathVariable(name = "menuId") Long menuId,
                                             @RequestPart(value = "menuImage") MultipartFile menuImage) {

        menuService.updateMenuImage(restaurantId ,menuId, menuImage);
        return ResponseEntity.ok(ApiUtil.from("메뉴 이미지 수정 완료."));
    }

    /* 메뉴 업데이트 */
    @Override
    @PutMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<?> updateMenu(@Valid@RequestBody MenuUpdateDto menuUpdateDto,
                                         @PathVariable(name = "restaurantId") Long restaurantId,
                                         @PathVariable(name = "menuId") Long menuId) {

        menuService.updateMenu(restaurantId, menuId, menuUpdateDto);
        return ResponseEntity.ok(ApiUtil.from("메뉴 업데이트 완료."));
    }

    /* 메뉴 삭제 */
    @Override
    @DeleteMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable(name = "restaurantId") Long restaurantId,
                                         @PathVariable(name = "menuId") Long menuId) {

        menuService.deleteMenu(restaurantId, menuId);
        return ResponseEntity.ok(ApiUtil.from("메뉴 삭제 완료."));
    }
}
