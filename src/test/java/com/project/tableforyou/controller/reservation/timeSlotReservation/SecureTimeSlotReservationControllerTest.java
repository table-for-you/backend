package com.project.tableforyou.controller.reservation.timeSlotReservation;

import com.project.tableforyou.domain.reservation.controller.SecureTimeSlotReservationController;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.service.TimeSlotReservationService;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.utils.api.ApiUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

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

@WebMvcTest(SecureTimeSlotReservationController.class)
public class SecureTimeSlotReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeSlotReservationService timeSlotReservationService;

    private User user;
    private PrincipalDetails principalDetails;
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
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
    @DisplayName("특정 시간대 예약하기 테스트")
    void createReservationTest() throws Exception {
        // given
        Long restaurantId = 1L;
        TimeSlot timeSlot = TimeSlot.TEN_AM;

        doNothing().when(timeSlotReservationService).saveTimeSlotReservation(principalDetails.getUsername(), restaurantId, timeSlot);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/restaurants/{restaurantId}/timeslot-reservations", restaurantId)
                        .with(user(principalDetails))
                        .param("time-slot", String.valueOf(timeSlot))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("예약자 추가 성공."));
    }

    @Test
    @DisplayName("특정 시간대 가게 예약 확인 테스트 - 예약 o")
    void checkUserReservationTrueTest() throws Exception {
        // given
        Long restaurantId = 1L;
        TimeSlot timeSlot = TimeSlot.TEN_AM;

        given(timeSlotReservationService.isUserAlreadyInTimeSlot(principalDetails.getUsername(), restaurantId, timeSlot))
                .willReturn(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/restaurants/{restaurantId}/timeslot-reservations-check", restaurantId)
                        .with(user(principalDetails))
                        .param("time-slot", String.valueOf(timeSlot))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(true));
    }

    @Test
    @DisplayName("특정 시간대 가게 예약 확인 테스트 - 예약 x")
    void checkUserReservationFalseTest() throws Exception {
        // given
        Long restaurantId = 1L;
        TimeSlot timeSlot = TimeSlot.TEN_AM;

        given(timeSlotReservationService.isUserAlreadyInTimeSlot(principalDetails.getUsername(), restaurantId, timeSlot))
                .willReturn(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/restaurants/{restaurantId}/timeslot-reservations-check", restaurantId)
                        .with(user(principalDetails))
                        .param("time-slot", String.valueOf(timeSlot))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(false));
    }

    @Test
    @DisplayName("특정 시간대 가게 예약 삭제 테스트")
    void deleteReservationTest() throws Exception {
        // given
        Long restaurantId = 1L;
        TimeSlot timeSlot = TimeSlot.TEN_AM;

        doNothing().when(timeSlotReservationService)
                .deleteTimeSlotReservation(restaurantId, principalDetails.getUsername(), timeSlot);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/restaurants/{restaurantId}/timeslot-reservations", restaurantId)
                        .with(user(principalDetails))
                        .param("time-slot", String.valueOf(timeSlot))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("예약자 삭제 성공."));
    }

    @Test
    @DisplayName("특정 시간대 가게 예약 삭제 실패 테스트 - 예약 안함")
    void deleteReservationFailedTest() throws Exception {
        // given
        Long restaurantId = 1L;
        TimeSlot timeSlot = TimeSlot.TEN_AM;

        doThrow(new CustomException(ErrorCode.RESERVATION_NOT_FOUND)).when(timeSlotReservationService)
                .deleteTimeSlotReservation(restaurantId, principalDetails.getUsername(), timeSlot);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/restaurants/{restaurantId}/timeslot-reservations", restaurantId)
                        .with(user(principalDetails))
                        .param("time-slot", String.valueOf(timeSlot))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.RESERVATION_NOT_FOUND.getMessage()));
    }
}
