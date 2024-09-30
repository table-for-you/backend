package com.project.tableforyou.domain.auth.api;

import com.project.tableforyou.domain.auth.dto.LoginDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static com.project.tableforyou.common.utils.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static com.project.tableforyou.common.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;

@Tag(name = "[인증 관련 API]", description = "인증 관련 API")
public interface AuthApi {

    @Operation(summary = "로그인", description = "사용자가 로그인 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "nickname": "테스터",
                                            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0NiIsInJvbGUiOiJPV05FUiIsImNhdGVnb3J5IjoiYWNjZXNzIiwidXNlcklkIjo2LCJpYXQiOjE3MjI2Njc1MzYsImV4cCI6MTcyMjY2OTMzNn0.9eY_1aSfKLfDhKN5X4f85N2hv_I65QOPFtq_2YXEhoA"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음 및 계정 잠김 및 비밀번호 틀림",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "UserNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """),
                            @ExampleObject(name = "LockedAccount", value = """
                                        {
                                            "status": 404,
                                            "message": "계정이 잠겨있습니다. 5분 후 다시 시도해 주세요."
                                        }
                                    """),
                            @ExampleObject(name = "InValidPassword", value = """
                                        {
                                            "status": 404,
                                            "message": "잘못된 비밀번호입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response);

    @Operation(summary = "accessToken 재발급", description = "서버 인증을 위한 accessToken 재발급을 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0NiIsInJvbGUiOiJPV05FUiIsImNhdGVnb3J5IjoiYWNjZXNzIiwidXNlcklkIjo2LCJpYXQiOjE3MjI2Njc1MzYsImV4cCI6MTcyMjY2OTMzNn0.9eY_1aSfKLfDhKN5X4f85N2hv_I65QOPFtq_2YXEhoA"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "RefreshToken 존재 x 및 만료",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "RefreshTokenNotFound", value = """
                                        {
                                            "status": 404,
                                            "message": "RefreshToken이 존재하지 않습니다."
                                        }
                                    """),
                            @ExampleObject(name = "ExpiredRefreshToken", value = """
                                        {
                                            "status": 404,
                                            "RefreshToken이 만료되었습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "로그아웃", description = "사용자가 로그아웃하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "로그아웃 되었습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "refreshToken or accessToken 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "Token이 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> logout(@RequestHeader(value = ACCESS_HEADER_VALUE, required = false) String accessToken,
                              @CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken,
                              HttpServletResponse response);

    @Operation(summary = "아이디 찾기", description = "사용자의 아이디를 찾기 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 찾기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "test"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 존재 하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> findingId(@RequestParam("email") @Valid @Email String email);

    @Operation(summary = "비밀번호 찾기", description = "사용자의 비밀번호를 찾기 위한 API입니다. <br> 임시 비밀번호를 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 찾기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "잠시 후 등록하신 메일로 임시 비밀번호가 도착합니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "등록된 아이디, 이메일 일치 x",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "정보를 정확히 입력해주세요."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> findPass(@RequestParam("email") @Valid @Email String email,
                                    @RequestParam("username") String username);

    @Operation(summary = "사용자 권한 확인", description = "사용자의 권한을 확인하기 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 권한 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Role-OWNER", value = """
                                        {
                                            "role": "OWNER"
                                        }
                                    """),
                            @ExampleObject(name = "Role-USER", value = """
                                        {
                                            "role": "USER"
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getUserRole(@RequestHeader("Authorization") String token);
}
