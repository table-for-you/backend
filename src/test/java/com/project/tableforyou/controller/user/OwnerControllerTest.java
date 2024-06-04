package com.project.tableforyou.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.dto.TimeSlotReservationResDto;
import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.entity.TimeSlotReservation;
import com.project.tableforyou.domain.reservation.service.OwnerReservationService;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.domain.reservation.service.TimeSlotReservationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @MockBean
    private TimeSlotReservationService timeSlotReservationService;

    @MockBean
    private OwnerReservationService ownerReservationService;

    private PrincipalDetails principalDetails;
    private User user;

    private QueueReservation reservation1;
    private QueueReservation reservation2;
    private QueueReservation reservation3;

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

        createReservation();

        user = User.builder()
                .username("owner")
                .password("password")
                .role(Role.OWNER)
                .build();

        principalDetails = new PrincipalDetails(user);
    }

    void createReservation() {
        this.reservation1 = QueueReservation.builder()
                .username("test1")
                .booking(1)
                .build();

        this.reservation2 = QueueReservation.builder()
                .username("test2")
                .booking(2)
                .build();

        this.reservation3 = QueueReservation.builder()
                .username("test3")
                .booking(3)
                .build();
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

        given(ownerRestaurantService.saveRestaurant(eq(user.getUsername()), any(RestaurantRequestDto.class))).willReturn(1L);

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
                .andExpect(jsonPath("$.response").value(1));
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
                .andExpect(jsonPath("$.response").value("가게 수정 완료."));
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
                .andExpect(jsonPath("$.response").value("가게 삭제 완료."));
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
    @DisplayName("해당 가게 예약자 불러오기(번호표) 테스트")
    public void getAllQueueReservationTest() throws Exception {
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
        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(true);
        given(queueReservationService.findAllQueueReservations(restaurantId)).willReturn(reservations);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/owner/restaurants/{restaurantId}/queue-reservations", restaurantId)
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

    @Test
    @DisplayName("해당 가게 예약자 불러오기(번호표) 실패 테스트 - 권한 없음")
    public void getAllQueueReservationFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;

        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/owner/restaurants/{restaurantId}/queue-reservations", restaurantId)
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("예약 순서 미루기(번호표) 테스트")
    void postponedGuestBookingByOwner() throws Exception {
        // given
        Long restaurantId = 1L;

        List<QueueReservationResDto> reservations = List.of(
                new QueueReservationResDto(reservation1),
                new QueueReservationResDto(reservation2),
                new QueueReservationResDto(reservation3)
        );

        QueueReservationReqDto reservationReqDto = new QueueReservationReqDto();
        reservationReqDto.setBooking(3);

        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(true);

        given(queueReservationService
                .getQueueReservations(eq(restaurantId), eq(principalDetails.getUsername()), any(QueueReservationReqDto.class)))
                .willReturn(reservations);

        given(queueReservationService.decreaseBooking(reservations, restaurantId)).willReturn(null);

        doNothing().when(queueReservationService)
                .postponedGuestBooking(eq(restaurantId), eq(principalDetails.getUsername()), any(QueueReservationReqDto.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/owner/restaurants/{restaurantId}/queue-reservations/postponed-guest-booking/{username}",
                        restaurantId, user.getUsername())
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationReqDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("예약자 미루기 + 앞당기기 성공."));
    }

    @Test
    @DisplayName("예약 순서 미루기(번호표) 실패 테스트 - 권한 없음")
    public void postponedGuestBookingFailedByOwnerTest() throws Exception {
        // given
        Long restaurantId = 1L;

        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(false);

        QueueReservationReqDto reservationReqDto = new QueueReservationReqDto();
        reservationReqDto.setBooking(3);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/owner/restaurants/{restaurantId}/queue-reservations/postponed-guest-booking/{username}",
                        restaurantId, user.getUsername())
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationReqDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("예약 삭제(번호표) 테스트")
    void deleteQueueReservationByOwnerTest() throws Exception {
        // given
        Long restaurantId = 1L;

        List<QueueReservationResDto> reservations = List.of(
                new QueueReservationResDto(reservation1),
                new QueueReservationResDto(reservation2),
                new QueueReservationResDto(reservation3)
        );

        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(true);

        given(queueReservationService
                .getQueueReservations(restaurantId, principalDetails.getUsername(), null))
                .willReturn(reservations);

        given(queueReservationService.decreaseBooking(reservations, restaurantId)).willReturn(null);

        doNothing().when(queueReservationService)
                .deleteQueueReservation(restaurantId, principalDetails.getUsername());

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/owner/restaurants/{restaurantId}/queue-reservations/{username}", restaurantId, user.getUsername())
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("예약자 삭제 성공."));
    }

    @Test
    @DisplayName("예약 삭제 실패(번호표) 테스트 - 권한 없음")
    public void deleteQueueReservationByOwnerFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;

        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/owner/restaurants/{restaurantId}/queue-reservations/{username}", restaurantId, user.getUsername())
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("해당 가게 예약자 불러오기(시간대) 테스트")
    public void getAllTimeSlotReservationTest() throws Exception {
        // given
        Long restaurantId = 1L;

        List<TimeSlotReservationResDto> reservations = List.of(
                new TimeSlotReservationResDto(TimeSlotReservation.builder()
                        .username("user1")
                        .timeSlot(TimeSlot.TEN_AM)
                        .build()),
                new TimeSlotReservationResDto(TimeSlotReservation.builder()
                        .timeSlot(TimeSlot.TEN_AM)
                        .username("user2")
                        .build())
        );
        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(true);
        given(timeSlotReservationService.findAllTimeSlotReservations(eq(restaurantId), any(TimeSlot.class))).willReturn(reservations);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/owner/restaurants/{restaurantId}/timeslot-reservations", restaurantId)
                        .with(user(principalDetails))
                        .param("time-slot", "TEN_AM")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @DisplayName("해당 가게 예약자 불러오기(시간대) 실패 테스트 - 권한 없음")
    public void getAllTimeSlotReservationFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;

        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/owner/restaurants/{restaurantId}/timeslot-reservations", restaurantId)
                        .with(user(principalDetails))
                        .param("time-slot", "TEN_AM")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("예약 삭제(시간대) 테스트")
    void deleteTimeSlotReservationByOwnerTest() throws Exception {
        // given
        Long restaurantId = 1L;

        List<TimeSlotReservationResDto> reservations = List.of(
                new TimeSlotReservationResDto(TimeSlotReservation.builder()
                        .username("user1")
                        .timeSlot(TimeSlot.TEN_AM)
                        .build()),
                new TimeSlotReservationResDto(TimeSlotReservation.builder()
                        .timeSlot(TimeSlot.TEN_AM)
                        .username("user2")
                        .build())
        );

        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(true);


        doNothing().when(timeSlotReservationService)
                .deleteTimeSlotReservation(eq(restaurantId), eq(user.getUsername()), any(TimeSlot.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/owner/restaurants/{restaurantId}/timeslot-reservations/{username}", restaurantId, user.getUsername())
                        .with(user(principalDetails))
                        .param("time-slot", "TEN_AM")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("예약자 삭제 성공."));
    }

    @Test
    @DisplayName("예약 삭제(시간대) 실패 테스트 - 권한 없음")
    public void deleteTimeSlotReservationByOwnerFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;

        given(ownerReservationService.isOwnerRestaurant(restaurantId, user.getUsername())).willReturn(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/owner/restaurants/{restaurantId}/timeslot-reservations/{username}", restaurantId, user.getUsername())
                        .with(user(principalDetails))
                        .param("time-slot", "TEN_AM")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
    }
}
