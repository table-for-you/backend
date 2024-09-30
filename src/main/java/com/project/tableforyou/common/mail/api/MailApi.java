package com.project.tableforyou.common.mail.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[메일 API]", description = "메일 관련 API")
public interface MailApi {

    @Operation(summary = "이메일 인증 번호 보내기", description = "이메일 인증 번호 보내기위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메일 보내기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "인증메일 보내기 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "이메일 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "잘못된 이메일입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "429", description = "1분 내에 보낸 적 있음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 429,
                                            "message": "1분 후 재전송 해주세요."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<String> sendCodeToMail(@RequestParam("email") @Valid @Email String email);

    @Operation(summary = "인증 번호 확인하기", description = "인증 번호 확인하기위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "checkTrue", value = """
                                        {
                                            "response": "true"
                                        }
                                    """),
                            @ExampleObject(name = "checkFalse", value = """
                                        {
                                            "response": "false"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "인증번호 일치하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 400,
                                            "message": "인증번호가 일치하지 않습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "message": "이미 존재하는 이메일입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "410", description = "유효시간 지남",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 410,
                                            "message": "유효시간이 지났습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> verifyCode(@RequestParam(value = "email") @Valid @Email String email,
                                 @RequestParam("code") String code);
}
