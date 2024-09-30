package com.project.tableforyou.controller.like;

import com.project.tableforyou.domain.like.controller.LikeController;
import com.project.tableforyou.domain.like.service.LikeService;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.security.auth.PrincipalDetails;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    private User user;
    private PrincipalDetails principalDetails;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .build();

        this.user = User.builder()
                .username("daeyoung")
                .password("password")
                .role(Role.USER)
                .build();

        this.principalDetails = new PrincipalDetails(user);
    }

    @Test
    @DisplayName("가게 좋아요 테스트")
    void likeRestaurantTest() throws Exception {
        // given
        Long restaurantId = 1L;

        doNothing().when(likeService).likeRestaurant(principalDetails.getUsername(), restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/restaurants/{restaurantId}/like", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("가게 좋아요 증가."));
    }

    @Test
    @DisplayName("가게 좋아요 실패 테스트 - 해당 가게 좋아요 이력 있음")
    void likeRestaurantFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;

        doThrow(new CustomException(ErrorCode.ALREADY_LIKE_RESTAURANT))
                .when(likeService).likeRestaurant(principalDetails.getUsername(), restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/restaurants/{restaurantId}/like", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_LIKE_RESTAURANT.getMessage()));
    }

    @Test
    @DisplayName("가게 좋아요 감소 테스트")
    void unLikeRestaurantTest() throws Exception {
        // given
        Long restaurantId = 1L;

        doNothing().when(likeService).unLikeRestaurant(principalDetails.getUsername(), restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/restaurants/{restaurantId}/like", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("가게 좋아요 감소."));
    }

    @Test
    @DisplayName("가게 좋아요 감소 실패 테스트 - 해당 가게 좋아요 이력 없음")
    void unLikeRestaurantFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;

        doThrow(new CustomException(ErrorCode.LIKE_NOT_FOUND))
                .when(likeService).unLikeRestaurant(principalDetails.getUsername(), restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/restaurants/{restaurantId}/like", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.LIKE_NOT_FOUND.getMessage()));
    }
}
