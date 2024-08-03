package com.project.tableforyou.domain.menu.api;

import com.project.tableforyou.domain.menu.dto.MenuRequestDto;
import com.project.tableforyou.domain.menu.dto.MenuUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[(권한 필요 o) 메뉴 API]", description = "권한이 필요한 메뉴 관련 API")
public interface SecureMenuApi {

    @Operation(summary = "메뉴 생성하기 *", description = "메뉴를 생성하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "1"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "메뉴 생성 권한 없음 (사장x)",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """))),
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
    ResponseEntity<?> createMenu(@Valid @RequestBody MenuRequestDto menuDto,
                                 @PathVariable(name = "restaurantId") Long restaurantId);


    @Operation(summary = "메뉴 업데이트하기 *", description = "메뉴를 업데이트하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 업데이트 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "메뉴 업데이트 완료."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "메뉴 생성 권한 없음 (사장x)",
                    content = @Content(mediaType = "application/json", examples =
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """))),
            @ApiResponse(responseCode = "404", description = "메뉴 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 메뉴입니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> updateMenu(@Valid@RequestBody MenuUpdateDto menuUpdateDto,
                                 @PathVariable(name = "restaurantId") Long restaurantId,
                                 @PathVariable(name = "menuId") Long menuId);

    @Operation(summary = "메뉴 삭제하기 *", description = "메뉴를 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "메뉴 삭제 완료."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "메뉴 생성 권한 없음 (사장x)",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "접근 권한이 없습니다."
                                        }
                                    """))),
            @ApiResponse(responseCode = "404", description = "메뉴 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 메뉴입니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> deleteMenu(@PathVariable(name = "restaurantId") Long restaurantId,
                                 @PathVariable(name = "menuId") Long menuId);
}
