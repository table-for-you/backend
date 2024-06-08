package com.project.tableforyou.controller.visit;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.visit.controller.VisitController;
import com.project.tableforyou.domain.visit.dto.VisitResponseDto;
import com.project.tableforyou.domain.visit.entity.Visit;
import com.project.tableforyou.domain.visit.service.VisitService;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
public class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    private User user;
    private PrincipalDetails principalDetails;

    private Visit visit1;
    private Visit visit2;
    private Visit visit3;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
                .build();

        createVisit();

        this.user = User.builder()
                .username("daeyoung")
                .password("password")
                .role(Role.USER)
                .build();

        this.principalDetails = new PrincipalDetails(user);
    }

    void createVisit() {
        this.visit1 = Visit.builder()
                .restaurant(new Restaurant())
                .build();

        this.visit2 = Visit.builder()
                .restaurant(new Restaurant())
                .build();

        this.visit3 = Visit.builder()
                .restaurant(new Restaurant())
                .build();
    }

    @Test
    @DisplayName("사용자 방문 가게 불러오기 테스트")
    void readVisitRestaurantTest() throws Exception {
        // given
        List<VisitResponseDto> visits = List.of(
                new VisitResponseDto(visit1),
                new VisitResponseDto(visit2),
                new VisitResponseDto(visit3)
        );

        given(visitService.userVisitRestaurants(principalDetails.getUsername())).willReturn(visits);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/users/restaurants")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

    }
}
