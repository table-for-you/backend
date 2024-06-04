package com.project.tableforyou.controller.reservation.queueReservation;

import com.project.tableforyou.domain.reservation.controller.PublicQueueReservationController;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.domain.visit.service.VisitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicQueueReservationController.class)
public class PublicQueueReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueReservationService queueReservationService;

    @MockBean
    private VisitService visitService;

    private QueueReservation reservation1;
    private QueueReservation reservation2;
    private QueueReservation reservation3;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        createReservation();
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
    @DisplayName("예약자 앞으로 당기기 테스트")
    void decreaseBookingTest() throws Exception {
        // given
        Long restaurantId = 1L;

        List<QueueReservationResDto> reservations = List.of(
                new QueueReservationResDto(reservation1),
                new QueueReservationResDto(reservation2),
                new QueueReservationResDto(reservation3)
        );

        given(queueReservationService.getQueueReservations(restaurantId, null, null)).willReturn(reservations);
        given(queueReservationService.decreaseBooking(reservations, restaurantId)).willReturn(reservation1.getUsername());
        doNothing().when(visitService).saveVisitRestaurant(reservation1.getUsername(), restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/public/restaurants/{restaurantId}/queue-reservations/decrease-booking", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(reservation1.getUsername() + "님 입장"));
    }
}
