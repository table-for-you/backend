package com.project.tableforyou.domain.user.apl;

import com.project.tableforyou.domain.user.dto.SignUpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[(권한 필요 x) 사용자 API]", description = "권한이 필요없는 사용자 관련 API")
public interface PublicUserApi {

    @Operation(summary = "회원가입", description = "서비스 최초 이용시 회원가입을 하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "회원가입 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "nickname": "닉네임은 필수 입력 값입니다.",
                                            "username": "아이디는 필수 입력 값입니다.",
                                            "password": "비밀번호는 필수 입력 값입니다.",
                                            "email": "이메일은 필수 입력 값입니다.",
                                            "age": "나이는 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            "nickname": "닉네임은 특수문자를 제외한 2~10자리여야 합니다.",
                                            "username": "아이디는 특수문자를 제외한 4~20자리여야 합니다.",
                                            "password": "비밀번호는 8~16자 영문자, 숫자, 특수문자를 사용하세요."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> register(@Valid @RequestBody SignUpDto signUpDto);

    @Operation(summary = "아이디 중복 확인하기", description = "회원가입 및 정보 업데이트 중, 중복된 아이디가 있는지 확인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 중복확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "existsUsername", value = """
                                        {
                                            "response": "true"
                                        }
                                    """),
                            @ExampleObject(name = "notExistsUsername", value = """
                                        {
                                            "response": "false"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 400,
                                            "message": "아이디는 특수문자를 제외한 4~20자리여야 합니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> checkUsernameExists(@RequestParam("username") String username);

    @Operation(summary = "닉네임 중복 확인하기", description = "회원가입 및 정보 업데이트 중, 중복된 닉네임이 있는지 확인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 중복확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "existsNickname", value = """
                                        {
                                            "response": "true"
                                        }
                                    """),
                            @ExampleObject(name = "notExistsNickname", value = """
                                        {
                                            "response": "false"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 400,
                                            "message": "닉네임은 특수문자를 제외한 2~10자리여야 합니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> checkNicknameExists(@RequestParam("nickname") String nickname);
}
