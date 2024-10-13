package com.project.tableforyou.domain.user.apl;

import com.project.tableforyou.domain.user.dto.FcmTokenRequestDto;
import com.project.tableforyou.domain.user.dto.PasswordDto;
import com.project.tableforyou.domain.user.dto.UserPasswordDto;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[(권한 필요 o) 사용자 API]", description = "권한이 필요한 사용자 관련 API")
public interface SecureUserApi {

    @Operation(summary = "자신의 정보 불러오기 *", description = "로그인된 사용자의 정보를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정보 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "id": 1,
                                            "username": "test",
                                            "email": "test@naver.com",
                                            "nickname": "테스터",
                                            "age": "20",
                                            "role": "USER",
                                            "createdTime": "2023-03-05T12:00:00",
                                            "modifiedTime": "2023-04-05T12:00:00"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> readUser(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "현재 비밀번호 검사하기 *", description = "로그인된 사용자의 현재 비밀번호를 검사하는 API입니다." +
                                                            "<br>회원 정보 수정시, 사용될 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현재 비밀번호 검사 성공",
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
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> checkPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                    @RequestBody PasswordDto passwordDto);

    @Operation(summary = "회원 정보 수정하기 *", description = "로그인된 사용자의 정보를 수정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정보 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "회원 업데이트 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "nickname": "닉네임은 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            "nickname": "닉네임은 특수문자를 제외한 2~10자리여야 합니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                 @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "회원 정보 수정하기 *", description = "로그인된 사용자의 정보를 수정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정보 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "회원 업데이트 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "currentPassword": "현재 비밀번호를 입력해주세요.",
                                            "newPassword": "비밀번호는 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            "newPassword": "비밀번호는 8~16자 영문자, 숫자, 특수문자를 사용하세요."
                                        }
                                    """),
                            @ExampleObject(name = "notEquals", value = """
                                        {
                                            "status": 400,
                                            "message": "현재 비밀번호를 잘못 입력하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> passwordUpdate(@Valid @RequestBody UserPasswordDto userPasswordDto,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "회원 정보 삭제하기 *", description = "로그인된 사용자의 정보를 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정보 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "회원 삭제 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "좋아요한 가게 불러오기 *", description = "로그인된 사용자가 좋아요한 가게를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요한 가게 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        [
                                            {
                                                "id": 1,
                                                "name": "가게1"
                                            },
                                            {
                                                "id": 2,
                                                "name": "가게2"
                                            }
                                        ]
                                    """)
                    }))
    })
    ResponseEntity<?> getRestaurantLike(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "fcmToken 저장 *", description = "fcmToken 저장히는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "fcmToken 저장 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "fcmToken 저장 완료."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 회원입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> saveFcmToken(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                   @RequestBody FcmTokenRequestDto fcmTokenRequestDto);
}
