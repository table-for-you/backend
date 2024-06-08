package com.project.tableforyou.controller.menu;

import com.project.tableforyou.domain.menu.controller.PublicMenuController;
import com.project.tableforyou.domain.menu.dto.MenuResponseDto;
import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.menu.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicMenuController.class)
public class PublicMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    private Menu menu1;
    private Menu menu2;
    private Menu menu3;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        createMenu();
    }

    void createMenu() {
        this.menu1 = Menu.builder()
                .id(1L)
                .name("메뉴1")
                .price("1,000").build();

        this.menu2 = Menu.builder()
                .id(2L)
                .name("test1")
                .price("2,000").build();

        this.menu3 = Menu.builder()
                .id(3L)
                .name("메뉴2")
                .price("3,000").build();
    }

    @Test
    @DisplayName("메뉴 검색하기 테스트")
    void readAllMenuTest() throws Exception {
        // given
        Long restaurantId = 1L;

        Page<MenuResponseDto> menus = new PageImpl<>(List.of(
                new MenuResponseDto(menu1),
                new MenuResponseDto(menu2),
                new MenuResponseDto(menu3)
        ));

        given(menuService.readAllMenu(eq(restaurantId), any(Pageable.class))).willReturn(menus);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants/{restaurantId}/menus", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value(menu1.getName()))
                .andExpect(jsonPath("$.content[1].name").value(menu2.getName()))
                .andExpect(jsonPath("$.content[2].name").value(menu3.getName()));
    }

    @Test
    @DisplayName("메뉴 검색하기 테스트 - 메뉴명 검색")
    void readAllMenuByNameTest() throws Exception {
        // given
        Long restaurantId = 1L;
        String searchKeyword = "메뉴";

        Page<MenuResponseDto> menus = new PageImpl<>(List.of(
                new MenuResponseDto(menu1),
                new MenuResponseDto(menu3)
        ));

        given(menuService.menuPageSearchList(eq(restaurantId), eq(searchKeyword), any(Pageable.class))).willReturn(menus);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants/{restaurantId}/menus", restaurantId)
                        .param("search-keyword", searchKeyword)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value(menu1.getName()))
                .andExpect(jsonPath("$.content[1].name").value(menu3.getName()));
    }

}
