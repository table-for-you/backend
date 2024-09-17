package com.project.tableforyou.controller.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.menu.controller.SecureMenuController;
import com.project.tableforyou.domain.menu.dto.MenuRequestDto;
import com.project.tableforyou.domain.menu.dto.MenuUpdateDto;
import com.project.tableforyou.domain.menu.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecureMenuController.class)
public class SecureMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("메뉴 생성 테스트")
    void createMenuTest() throws Exception {
        // given
        Long restaurantId = 1L;
        Long menuId = 1L;
        MenuRequestDto menuRequestDto = MenuRequestDto.builder()
                .name("메뉴")
                .price("5,000")
                .build();

        given(menuService.saveMenu(eq(restaurantId), any(MenuRequestDto.class), any(MultipartFile.class))).willReturn(1L);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/restaurants/{restaurantId}/menus", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuRequestDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(menuId));
    }

    @Test
    @DisplayName("메뉴 생성 실패 테스트 - 메뉴 이름, 가격 빈칸")
    void createMenuInvalidTest() throws Exception {
        // given
        Long restaurantId = 1L;

        MenuRequestDto menuRequestDto = MenuRequestDto.builder()
                .name("")
                .price("")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/restaurants/{restaurantId}/menus", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuRequestDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("메뉴명은 필수 입력입니다."))
                .andExpect(jsonPath("$.price").value("가격은 필수 입력입니다."));
    }

    @Test
    @DisplayName("메뉴 업데이트 테스트")
    void updateMenuTest() throws Exception {
        // given
        Long restaurantId = 1L;
        Long menuId = 1L;

        MenuUpdateDto menuUpdateDto = MenuUpdateDto.builder()
                .name("메뉴 업데이트")
                .price("3,000")
                .build();

        doNothing().when(menuService).updateMenu(eq(restaurantId), eq(menuId), eq(menuUpdateDto));

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/restaurants/{restaurantId}/menus/{menuId}", restaurantId, menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuUpdateDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("메뉴 업데이트 완료."));
    }

    @Test
    @DisplayName("메뉴 업데이트 실패 테스트 - 메뉴 이름, 가격 빈칸")
    void updateMenuInvalidTest() throws Exception {
        // given
        Long restaurantId = 1L;
        Long menuId = 1L;

        MenuUpdateDto menuUpdateDto = MenuUpdateDto.builder()
                .name("")
                .price("")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/restaurants/{restaurantId}/menus/{menuId}", restaurantId, menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuUpdateDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("메뉴명은 필수 입력입니다."))
                .andExpect(jsonPath("$.price").value("가격은 필수 입력입니다."));
    }

    @Test
    @DisplayName("메뉴 삭제 테스트")
    void deleteMenuTest() throws Exception {
        // given
        Long restaurantId = 1L;
        Long menuId = 1L;

        doNothing().when(menuService).deleteMenu(restaurantId, menuId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/restaurants/{restaurantId}/menus/{menuId}", restaurantId, menuId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("메뉴 삭제 완료."));
    }
}
