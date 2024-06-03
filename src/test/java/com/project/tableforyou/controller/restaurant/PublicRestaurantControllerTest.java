package com.project.tableforyou.controller.restaurant;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.domain.restaurant.controller.PublicRestaurantController;
import com.project.tableforyou.domain.restaurant.dto.RestaurantInfoDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.restaurant.service.RestaurantService;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.utils.redis.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
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

import java.util.ArrayList;
import java.util.List;

import static com.project.tableforyou.utils.redis.RedisProperties.RESERVATION_KEY_PREFIX;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicRestaurantController.class)
@AutoConfigureDataJpa
public class PublicRestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private QueueReservationService queueReservationService;

    @MockBean
    private RedisUtil redisUtil;

    private User user;
    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private Restaurant restaurant3;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        createRestaurant();
    }

    void createRestaurant() {

        this.user = User.builder()
                .name("user1")
                .nickname("사용자1")
                .role(Role.OWNER)
                .build();

        this.restaurant1 = Restaurant.builder()
                .id(1L)
                .name("가게1")
                .time("09:00~17:00")
                .region(Region.DAEGU)
                .location("대구 중구")
                .user(user)
                .rating(3.5)
                .status(RestaurantStatus.PENDING)
                .likes(new ArrayList<>())
                .totalSeats(10)
                .usedSeats(5)
                .build();

        this.restaurant2 = Restaurant.builder()
                .id(2L)
                .name("가게2")
                .time("09:00~17:00")
                .region(Region.SEOUL)
                .location("서울 강남")
                .user(user)
                .rating(4.5)
                .status(RestaurantStatus.PENDING)
                .likes(new ArrayList<>())
                .totalSeats(10)
                .usedSeats(10)
                .build();

        this.restaurant3 = Restaurant.builder()
                .id(3L)
                .name("Restaurant3")
                .time("09:00~17:00")
                .region(Region.DAEGU)
                .location("대구 중구")
                .user(user)
                .rating(2.5)
                .status(RestaurantStatus.PENDING)
                .likes(new ArrayList<>())
                .totalSeats(10)
                .usedSeats(0)
                .build();
    }
    @Test
    @DisplayName("가게 불러오기 테스트")
    void readRestaurantTest() throws Exception {
        // given
        RestaurantResponseDto restaurantDto = new RestaurantResponseDto(restaurant1);

        given(restaurantService.readRestaurant(restaurant1.getId())).willReturn(restaurantDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants/{restaurantId}", restaurant1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurant1.getId()))
                .andExpect(jsonPath("$.name").value(restaurant1.getName()));
    }

    @Test
    @DisplayName("전체 가게 불러오기 테스트")
    void readAllRestaurantTest() throws Exception {
        // given
        Page<RestaurantInfoDto> restaurants = new PageImpl<>(List.of(
                new RestaurantInfoDto(restaurant1),
                new RestaurantInfoDto(restaurant2),
                new RestaurantInfoDto(restaurant3)
        ));

        given(restaurantService.restaurantPageList(any(Pageable.class))).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "rating")
                        .param("direction", "DESC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("가게1"))
                .andExpect(jsonPath("$.content[1].name").value("가게2"))
                .andExpect(jsonPath("$.content[2].name").value("Restaurant3"));
    }

    @Test
    @DisplayName("전체 가게 불러오기 테스트 - 가게 이름")
    void readAllRestaurantByName() throws Exception {
        // given
        Page<RestaurantInfoDto> restaurants = new PageImpl<>(List.of(
                new RestaurantInfoDto(restaurant1),
                new RestaurantInfoDto(restaurant2)
        ));

        given(restaurantService.restaurantPageSearchList(anyString(), any(Pageable.class))).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type", "restaurant")
                        .param("search-keyword", "가게")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "rating")
                        .param("direction", "DESC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("가게1"))
                .andExpect(jsonPath("$.content[1].name").value("가게2"));
    }

    @Test
    @DisplayName("전체 가게 불러오기 테스트 - 지역 이름")
    void readAllRestaurantByRegion() throws Exception {
        // given
        Page<RestaurantInfoDto> restaurants = new PageImpl<>(List.of(
                new RestaurantInfoDto(restaurant1),
                new RestaurantInfoDto(restaurant3)
        ));

        given(restaurantService.restaurantPageListByRegion(anyString(), any(Pageable.class))).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type", "region")
                        .param("search-keyword", "DAEGU")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "rating")
                        .param("direction", "DESC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("가게1"))
                .andExpect(jsonPath("$.content[1].name").value("Restaurant3"));
    }

    @Test
    @DisplayName("전체 가게 불러오기 테스트 - 위치 이름")
    void readAllRestaurantByLocation() throws Exception {
        // given
        Page<RestaurantInfoDto> restaurants = new PageImpl<>(List.of(
                new RestaurantInfoDto(restaurant1),
                new RestaurantInfoDto(restaurant3)
        ));

        given(restaurantService.restaurantPageListByLocation(anyString(), any(Pageable.class))).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type", "location")
                        .param("search-keyword", "대구")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "rating")
                        .param("direction", "DESC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("가게1"))
                .andExpect(jsonPath("$.content[1].name").value("Restaurant3"));
    }

    @Test
    @DisplayName("전체 가게 불러오기 실패 테스트 - 검색 유형 불일치")
    void readAllRestaurantFailedInvalidTest() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type", "no-exists")
                        .param("search-keyword", ".")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "rating")
                        .param("direction", "DESC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()));
    }

    @Test
    @DisplayName("가게 예약자 수 불러오기 테스트")
    void readRestaurantWaitingNumTest() throws Exception {
        // given
        int restaurantWaitingNum = 10;
        given(queueReservationService.getQueueWaitingCount(restaurant1.getId())).willReturn(restaurantWaitingNum);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants/{restaurantId}/waiting", restaurant1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(restaurantWaitingNum)));
    }

    @Test
    @DisplayName("Forward : 좌석 업데이트 테스트 - 좌석 증가")
    void updateUsedSeatsIncreaseForwardTest() throws Exception {
        // given
        Long restaurantId = restaurant1.getId();
        boolean increase = true;

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/public/restaurants/{restaurantId}/update-used-seats", restaurantId)
                        .param("increase", String.valueOf(increase))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/public/restaurants/" + restaurantId + "/update-used-seats/1"));
    }

    @Test
    @DisplayName("Forward : 좌석 업데이트 테스트 - 좌석 감소 및 예약자 없음 ")
    void updateFullUsedSeatsDecreaseWithoutReservationForwardTest() throws Exception {
        // given
        Long restaurantId = restaurant1.getId();
        boolean decrease = false;
        String key = RESERVATION_KEY_PREFIX + restaurantId;

        given(redisUtil.hashSize(key)).willReturn(0);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/public/restaurants/{restaurantId}/update-used-seats", restaurantId)
                        .param("increase", String.valueOf(decrease))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/public/restaurants/" + restaurantId + "/update-used-seats/-1"));
    }

    @Test
    @DisplayName("Forward : 좌석 업데이트 테스트 - 좌석 감소 및 예약자 확인")
    void updateUsedSeatsDecreaseTestWithReservationForwardTest() throws Exception {
        // given
        Long restaurantId = 1L;
        boolean decrease = false;
        String key = RESERVATION_KEY_PREFIX + restaurantId;

        given(redisUtil.hashSize(key)).willReturn(1);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/public/restaurants/{restaurantId}/update-used-seats", restaurantId)
                        .param("increase", String.valueOf(decrease))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/public/restaurants/" + restaurantId + "/reservations/decrease-booking"));;
    }

    @Test
    @DisplayName("좌석 업데이트 테스트 - 좌석 증가")
    void updateUsedSeatsIncreaseTest() throws Exception {
        // given
        Long restaurantId = restaurant1.getId();
        int value = 1;
        RestaurantResponseDto restaurantDto = new RestaurantResponseDto(restaurant1);

        given(restaurantService.readRestaurant(restaurantId)).willReturn(restaurantDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/public/restaurants/{restaurantId}/update-used-seats/{value}", restaurantId, value)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("가게 좌석 증가 완료."));
    }

    @Test
    @DisplayName("좌석 업데이트 테스트 - 좌석 감소")
    void updateUsedSeatsDecreaseTest() throws Exception {
        // given
        Long restaurantId = restaurant1.getId();
        int value = -1;
        RestaurantResponseDto restaurantDto = new RestaurantResponseDto(restaurant1);

        given(restaurantService.readRestaurant(restaurantId)).willReturn(restaurantDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/public/restaurants/{restaurantId}/update-used-seats/{value}", restaurantId, value)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("가게 좌석 감소 완료."));
    }

    @Test
    @DisplayName("좌석 업데이트 테스트 - 좌석 부족으로 증가 실패")
    void updateUsedSeatsIncreaseFailTest() throws Exception {
        // given
        Long restaurantId = restaurant2.getId();
        int value = 1;
        RestaurantResponseDto restaurantDto = new RestaurantResponseDto(restaurant2);

        given(restaurantService.readRestaurant(restaurantId)).willReturn(restaurantDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/public/restaurants/{restaurantId}/update-used-seats/{value}", restaurantId, value)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("가게 좌석 업데이트 실패."));
    }

    @Test
    @DisplayName("좌석 업데이트 테스트 - 예약 0으로 감소 실패")
    void updateUsedSeatsDecreaseFailTest() throws Exception {
        // given
        Long restaurantId = restaurant3.getId();
        int value = -1;
        RestaurantResponseDto restaurantDto = new RestaurantResponseDto(restaurant3);

        given(restaurantService.readRestaurant(restaurantId)).willReturn(restaurantDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/public/restaurants/{restaurantId}/update-used-seats/{value}", restaurantId, value)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("가게 좌석 업데이트 실패."));
    }
}
