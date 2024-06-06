package com.project.tableforyou.controller.reservation.timeSlotReservation;

import com.project.tableforyou.domain.reservation.controller.PublicTimeSlotReservationController;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.service.TimeSlotReservationService;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicTimeSlotReservationController.class)
public class PublicTimeSlotReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeSlotReservationService timeSlotReservationService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("특정 시간대 예약 가능 여부 확인 테스트 - 예약 가능")
    void checkTimeReservationNotFullTest() throws Exception {
        // given
        Long restaurantId = 1L;
        TimeSlot timeSlot = TimeSlot.TEN_AM;

        given(timeSlotReservationService.checkTimeSlotReservationFull(restaurantId, timeSlot)).willReturn(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants/{restaurantId}/timeslot-reservations-full-check", restaurantId)
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
    @DisplayName("특정 시간대 예약 가능 여부 확인 테스트 - 예약 불가")
    void checkTimeReservationFullTest() throws Exception {
        // given
        Long restaurantId = 1L;
        TimeSlot timeSlot = TimeSlot.TEN_AM;

        given(timeSlotReservationService.checkTimeSlotReservationFull(restaurantId, timeSlot)).willReturn(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/restaurants/{restaurantId}/timeslot-reservations-full-check", restaurantId)
                        .param("time-slot", String.valueOf(timeSlot))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(true));
    }

}
