package com.project.tableforyou.userController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.user.controller.PublicUserController;
import com.project.tableforyou.domain.user.dto.PasswordDto;
import com.project.tableforyou.domain.user.dto.SignUpDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {PublicUserController.class})
@AutoConfigureDataJpa
public class PublicUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void registerSuccessTest() throws Exception {
        // given
        SignUpDto userRequestDto = SignUpDto.builder()
                .name("박대영")
                .nickname("대영")
                .username("daeyoung")
                .password("QWEqwe123@")
                .email("daeyoung@google.com")
                .age("30")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/public/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 정보 누락")
    public void registerMissingTest() throws Exception {
        // given
        SignUpDto userRequestDto = SignUpDto.builder()
                .name("박대영")
                .nickname("대영")
                .username("daeyoung")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/public/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
        );

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("비밀번호는 필수 입력 값입니다."))
                .andExpect(jsonPath("$.email").value("이메일은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.age").value("나이는 필수 입력 값입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 조건 불충족")
    public void registerInvalidDataTest() throws Exception {
        // given
        SignUpDto userRequestDto = SignUpDto.builder()
                .name("")
                .nickname("대")
                .username("@#$")
                .password("1234")
                .email("")
                .age("")
                .build();


        // when
        ResultActions resultActions = mockMvc.perform(
                post("/public/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
        );

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("이름은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.nickname").value("닉네임은 특수문자를 제외한 2~10자리여야 합니다."))
                .andExpect(jsonPath("$.username").value("아이디는 특수문자를 제외한 4~20자리여야 합니다."))
                .andExpect(jsonPath("$.password").value("비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요."))
                .andExpect(jsonPath("$.email").value("이메일은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.age").value("나이는 필수 입력 값입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("닉네임 중복 확인 테스트")
    public void checkNicknameSuccessTest() throws Exception {
        // given
        String nickname = "대영";
        given(userService.existsByNickname(nickname)).willReturn(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/users/check-nickname")
                        .param("nickname", nickname)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 테스트 - 닉네임 조건 불충족")
    public void nicknameInvalidTest() throws Exception {
        // given
        String nickname = "";
        given(userService.existsByNickname(nickname)).willThrow(new CustomException(ErrorCode.INVALID_NICKNAME_PATTERN));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/users/check-nickname")
                        .param("nickname", nickname)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_NICKNAME_PATTERN.getMessage()));
    }

    @Test
    @DisplayName("아이디 중복 확인 테스트")
    public void checkUsernameSuccessTest() throws Exception {
        // given
        String username = "daeyoung";
        given(userService.existsByUsername(username)).willReturn(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/users/check-username")
                        .param("username", username)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }

    @Test
    @DisplayName("아이디 중복 테스트 - 닉네임 조건 불충족")
    public void UsernameInvalidTest() throws Exception {
        // given
        String username = "";
        given(userService.existsByUsername(username)).willThrow(new CustomException(ErrorCode.INVALID_USERNAME_PATTERN));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/public/users/check-username")
                        .param("username", username)
        );

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_USERNAME_PATTERN.getMessage()))
                .andDo(print());
    }


}