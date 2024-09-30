package com.project.tableforyou.controller.reservation.queueReservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.reservation.controller.SecureQueueReservationController;
import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SecureQueueReservationController.class)
public class SecureQueueReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QueueReservationService queueReservationService;

    private User user;
    private PrincipalDetails principalDetails;

    private QueueReservation reservation1;
    private QueueReservation reservation2;
    private QueueReservation reservation3;
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(put("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .build();

        createReservation();

        this.user = User.builder()
                .username("daeyoung")
                .password("password")
                .role(Role.USER)
                .build();

        this.principalDetails = new PrincipalDetails(user);
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
    @DisplayName("예약자 추가 테스트")
    void createReservationTest() throws Exception {
        // given
        Long restaurantId = 1L;

        doNothing().when(queueReservationService).saveQueueReservation(principalDetails.getUsername(), restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/restaurants/{restaurantId}/queue-reservations", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("예약자 추가 성공."));
    }

    @Test
    @DisplayName("예약자 추가 실패 테스트 - 이미 예약한 가게")
    void createReservationFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;

        doThrow(new CustomException(ErrorCode.ALREADY_USER_RESERVATION))
                .when(queueReservationService).saveQueueReservation(principalDetails.getUsername(), restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/restaurants/{restaurantId}/queue-reservations", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_USER_RESERVATION.getMessage()));
    }

    @Test
    @DisplayName("가게 예약 확인 테스트 - 예약 o")
    void checkUserReservationTrueTest() throws Exception {
        // given
        Long restaurantId = 1L;

        given(queueReservationService.isUserAlreadyInQueue(principalDetails.getUsername(), restaurantId))
                .willReturn(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/restaurants/{restaurantId}/queue-reservations-check", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("true"));
    }

    @Test
    @DisplayName("가게 예약 확인 테스트 - 예약 x")
    void checkUserReservationFalseTest() throws Exception {
        // given
        Long restaurantId = 1L;

        given(queueReservationService.isUserAlreadyInQueue(principalDetails.getUsername(), restaurantId))
                .willReturn(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/restaurants/{restaurantId}/queue-reservations-check", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("false"));
    }

    @Test
    @DisplayName("예약 순서 미루기 테스트")
    void postponedGuestBookingTest() throws Exception {
        // given
        Long restaurantId = 1L;

        List<QueueReservationResDto> reservations = List.of(
                new QueueReservationResDto(reservation1),
                new QueueReservationResDto(reservation2),
                new QueueReservationResDto(reservation3)
        );

        QueueReservationReqDto reservationReqDto = new QueueReservationReqDto();
        reservationReqDto.setBooking(3);

        given(queueReservationService
                .getQueueReservations(eq(restaurantId), eq(principalDetails.getUsername()), any(QueueReservationReqDto.class)))
                .willReturn(reservations);

        given(queueReservationService.decreaseBooking(reservations, restaurantId)).willReturn(null);

        doNothing().when(queueReservationService)
                .postponedGuestBooking(eq(restaurantId), eq(principalDetails.getUsername()), any(QueueReservationReqDto.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/restaurants/{restaurantId}/queue-reservations/postponed-guest-booking", restaurantId)
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
    @DisplayName("예약 순서 미루기 실패 테스트 - 잘못된 예약 순서 전달")
    void readAllQueueReservationFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;

        List<QueueReservationResDto> reservations = List.of(
                new QueueReservationResDto(reservation1),
                new QueueReservationResDto(reservation2),
                new QueueReservationResDto(reservation3)
        );

        QueueReservationReqDto reservationReqDto = new QueueReservationReqDto();
        reservationReqDto.setBooking(3);

        doThrow(new CustomException(ErrorCode.INVALID_PARAMETER)).when(queueReservationService)
                .getQueueReservations(eq(restaurantId), eq(principalDetails.getUsername()), any(QueueReservationReqDto.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/restaurants/{restaurantId}/queue-reservations/postponed-guest-booking", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationReqDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()));
    }

    @Test
    @DisplayName("예약 삭제 테스트")
    void deleteReservationTest() throws Exception {
        // given
        Long restaurantId = 1L;

        List<QueueReservationResDto> reservations = List.of(
                new QueueReservationResDto(reservation1),
                new QueueReservationResDto(reservation2),
                new QueueReservationResDto(reservation3)
        );

        given(queueReservationService
                .getQueueReservations(restaurantId, principalDetails.getUsername(), null))
                .willReturn(reservations);

        given(queueReservationService.decreaseBooking(reservations, restaurantId)).willReturn(null);

        doNothing().when(queueReservationService)
                .deleteQueueReservation(restaurantId, principalDetails.getUsername());

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/restaurants/{restaurantId}/queue-reservations", restaurantId)
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("예약자 삭제 성공."));
    }
}
