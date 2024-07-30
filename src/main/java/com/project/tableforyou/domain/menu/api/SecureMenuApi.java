package com.project.tableforyou.domain.menu.api;

import com.project.tableforyou.domain.menu.dto.MenuRequestDto;
import com.project.tableforyou.domain.menu.dto.MenuUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[(권한 필요 o) 메뉴 API]", description = "권한이 필요한 메뉴 관련 API")
public interface SecureMenuApi {

    @Operation(summary = "메뉴 생성하기 *", description = "메뉴를 생성하는 API입니다.")
    ResponseEntity<?> createMenu(@Valid @RequestBody MenuRequestDto menuDto,
                                 @PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "메뉴 업데이트하기 *", description = "메뉴를 업데이트하는 API입니다.")
    ResponseEntity<?> updateMenu(@Valid@RequestBody MenuUpdateDto menuUpdateDto,
                                 @PathVariable(name = "restaurantId") Long restaurantId,
                                 @PathVariable(name = "menuId") Long menuId);

    @Operation(summary = "메뉴 삭제하기 *", description = "메뉴를 삭제하는 API입니다.")
    ResponseEntity<?> deleteMenu(@PathVariable(name = "restaurantId") Long restaurantId,
                                 @PathVariable(name = "menuId") Long menuId);
}
