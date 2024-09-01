package com.project.tableforyou.controller.restaurant;

import com.project.tableforyou.domain.restaurant.controller.SecureRestaurantController;
import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.restaurant.service.RestaurantService;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SecureRestaurantController.class)
public class SecureRestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    private User user;
    private Restaurant restaurant;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        createObject();
    }

    void createObject() {
        this.user = User.builder()
                .nickname("용자")
                .role(Role.OWNER)
                .build();

        this.restaurant = Restaurant.builder()
                .id(1L)
                .name("가게")
                .time("09:00~17:00")
                .region(Region.DAEGU)
                .location("대구 중구")
                .user(user)
                .status(RestaurantStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("가게 평점 업데이트 성공 테스트")
    void updateRatingTest() throws Exception {
        // given
        Long restaurantId = restaurant.getId();
        double rating = 3.5;

        doNothing().when(restaurantService).updateRating(restaurantId, rating);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/restaurants/{restaurantId}/update-rating", restaurantId)
                        .param("rating", String.valueOf(rating))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("가게 평점 업데이트 완료."));
    }

    @Test
    @DisplayName("가게 평점 업데이트 실패 테스트 - 파라미터 오류")
    void updateRatingFailedInvalidTest() throws Exception {
        // given
        Long restaurantId = restaurant.getId();

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/restaurants/{restaurantId}/update-rating", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("서버 에러입니다. 서버 팀에 연락주세요."));
    }
}
