package com.project.tableforyou.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.service.OwnerRestaurantService;
import com.project.tableforyou.domain.user.controller.OwnerController;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.security.auth.PrincipalDetails;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
@AutoConfigureDataJpa
public class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OwnerRestaurantService ownerRestaurantService;

    @MockBean
    private QueueReservationService queueReservationService;

    private PrincipalDetails principalDetails;
    private User user;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(put("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .build();

        user = User.builder()
                .username("owner")
                .password("password")
                .role(Role.OWNER)
                .build();

        principalDetails = new PrincipalDetails(user);
    }

    @Test
    @DisplayName("가게 신청 성공 테스트")
    public void createRestaurantTest() throws Exception {
        // given
        RestaurantRequestDto requestDto = RestaurantRequestDto.builder()
                .name("가게이름")
                .time("09:00~18:00")
                .region(Region.DAEGU)
                .location("대구 중구")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/owner/restaurants")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("가게 신청이 완료 되었습니다. 승인을 기다려 주세요."));
    }

    @Test
    @DisplayName("가게 신청 실패 테스트 - 정보 누락")
    public void createRestaurantMissingTest() throws Exception {
        // given
        RestaurantRequestDto requestDto = RestaurantRequestDto.builder()
                .name("")
                .time("")
                .region(null)
                .location("")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/owner/restaurants")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("가게 이름은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.time").value("영업 시간은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.region").value("지역은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.location").value("위치 정보는 필수 입력 값입니다."));
    }

    @Test
    @DisplayName("사장 가게 불러오기 테스트")
    public void readOwnerRestaurantTest() throws Exception {
        // given
        List<RestaurantNameDto> restaurants = List.of(
                new RestaurantNameDto(Restaurant.builder().name("Restaurant1").build()),
                new RestaurantNameDto(Restaurant.builder().name("Restaurant2").build())
        );

        given(ownerRestaurantService.findByRestaurantOwner(user.getUsername())).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/owner/restaurants")
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Restaurant1"))
                .andExpect(jsonPath("$[1].name").value("Restaurant2"));
    }

    @Test
    @DisplayName("가게 정보 업데이트 성공 테스트")
    public void updateRestaurantTest() throws Exception {
        // given
        Long restaurantId = 1L;
        RestaurantUpdateDto updateDto = RestaurantUpdateDto.builder()
                .name("가게 이름 수정")
                .time("07:00~17:00")
                .region(Region.SEOUL)
                .location("서울 강남")
                .totalSeats(30)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/owner/restaurants/{restaurantId}", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("가게 수정 완료."));
    }

    @Test
    @DisplayName("가게 정보 업데이트 실패 테스트 - 정보 누락")
    public void updateRestaurantMissingTest() throws Exception {
        // given
        Long restaurantId = 1L;
        RestaurantUpdateDto updateDto = RestaurantUpdateDto.builder()
                .name("")
                .time("")
                .region(null)
                .location("")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/owner/restaurants/{restaurantId}", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("가게 이름은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.time").value("영업 시간은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.region").value("지역은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.location").value("위치 정보는 필수 입력 값입니다."));
    }

    @Test
    @DisplayName("가게 삭제 성공 테스트")
    public void deleteRestaurantTest() throws Exception {
        // given
        Long restaurantId = 1L;
        doNothing().when(ownerRestaurantService).deleteRestaurant(restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/owner/restaurants/{restaurantId}", restaurantId)
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("가게 삭제 완료."));
    }

    @Test
    @DisplayName("가게 삭제 실패 테스트 - 사용자 없음")
    public void deleteRestaurantFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;
        doThrow(new CustomException(ErrorCode.USER_NOT_FOUND))
                .when(ownerRestaurantService).deleteRestaurant(restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/owner/restaurants/{restaurantId}", restaurantId)
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("해당 가게 예약자 불러오기 테스트")
    public void getAllReservationTest() throws Exception {
        // given
        Long restaurantId = 1L;

        List<QueueReservationResDto> reservations = List.of(
                new QueueReservationResDto(QueueReservation.builder()
                        .booking(1)
                        .username("user1")
                        .build()),
                new QueueReservationResDto(QueueReservation.builder()
                        .booking(2)
                        .username("user2")
                        .build())
        );

        given(queueReservationService.findAllQueueReservations(restaurantId)).willReturn(reservations);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/owner/restaurants/{restaurantId}/reservations", restaurantId)
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].booking").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].booking").value(2))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }
}
