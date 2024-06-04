package com.project.tableforyou.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.like.service.LikeService;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.user.controller.SecureUserController;
import com.project.tableforyou.domain.user.dto.PasswordDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.service.UserService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {SecureUserController.class})
@AutoConfigureDataJpa
public class SecureUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private LikeService likeService;

    private User user;
    private PrincipalDetails principalDetails;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(put("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .build();


        user = User.builder()
                .username("daeyoung")
                .password("currentPassword")
                .role(Role.USER)
                .build();

        principalDetails = new PrincipalDetails(user);
    }

    @Test
    @DisplayName("사용자 정보 불러오기")
    public void readUserTest() throws Exception {
        // given
        UserResponseDto userResponseDto = new UserResponseDto(user);

        given(userService.readUser(user.getUsername())).willReturn(userResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/users")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    @DisplayName("현재 비밀번호 검사 테스트")
    public void checkPasswordTest() throws Exception {
        // given
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setPassword("currentPassword");

        given(userService.checkPass(eq(user.getUsername()), any(PasswordDto.class))).willReturn(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/users/check-password")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("true"));
    }

    @Test
    @DisplayName("회원 업데이트 성공 테스트")
    public void userUpdateTest() throws Exception {
        // given
        String updateNickname = "nickname12";
        String updatePassword = "pass123@";

        UserUpdateDto userUpdateDto = new UserUpdateDto();

        userUpdateDto.setNickname(updateNickname);
        userUpdateDto.setPassword(updatePassword);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/users")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("회원 업데이트 성공."));
    }

    @Test
    @DisplayName("회원 업데이트 실패 테스트 - 닉네임, 비밀번호 빈칸")
    public void userUpdateFailBlankTest() throws Exception {
        // given
        UserUpdateDto userUpdateDto = new UserUpdateDto();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/users")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto))
        );

        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nickname").value("닉네임은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.password").value("비밀번호는 필수 입력 값입니다."));
    }

    @Test
    @DisplayName("회원 업데이트 실패 테스트 - 닉네임, 비밀번호 패턴 불일치")
    public void userUpdateFailPatternTest() throws Exception {
        // given
        String updateNickname = "x";
        String updatePassword = "1234";

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setNickname(updateNickname);
        userUpdateDto.setPassword(updatePassword);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/users")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nickname").value("닉네임은 특수문자를 제외한 2~10자리여야 합니다."))
                .andExpect(jsonPath("$.password").value("비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요."));
    }

    @Test
    @DisplayName("회원 삭제 성공 테스트")
    public void userDeleteTest() throws Exception {
        // given
        doNothing().when(userService).deleteUser(user.getUsername());   // 반환형이 void이기에 doNoting, doThrow 등 사용

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/users")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("회원 삭제 성공."));
    }

    @Test
    @DisplayName("회원 삭제 실패 테스트 - 사용자 없음")
    public void userDeleteFailUserNotFoundTest() throws Exception {
        // given
        doThrow(new CustomException(ErrorCode.USER_NOT_FOUND)).when(userService).deleteUser(user.getUsername());

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/users")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("회원 삭제 실패 테스트 - 인증되지 않은 사용자")
    public void userDeleteFailUnauthenticatedTest() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @Test
    @DisplayName("좋아요한 가게 불러오기 테스트")
    public void likeRestaurantsTest() throws Exception {
        // given
        List<RestaurantNameDto> likeRestaurants = List.of(
                new RestaurantNameDto(Restaurant.builder().name("Restaurant1").build()),
                new RestaurantNameDto(Restaurant.builder().name("Restaurant2").build())
        );

        given(likeService.getRestaurantLike(user.getUsername())).willReturn(likeRestaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/users/like-restaurants")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Restaurant1"))
                .andExpect(jsonPath("$[1].name").value("Restaurant2"));
    }

    @Test
    @DisplayName("좋아요한 가게 불러오기 실패 테스트 - 인증되지 않은 사용자")
    public void likeRestaurantsFailUnauthenticatedTest() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/users/like-restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://localhost/login"));
    }
}
