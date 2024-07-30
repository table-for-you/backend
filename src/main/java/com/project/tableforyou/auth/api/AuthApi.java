package com.project.tableforyou.auth.api;

import com.project.tableforyou.auth.dto.LoginDto;
import com.project.tableforyou.auth.dto.UserRoleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[인증 관련 API]", description = "인증 관련 API")
public interface AuthApi {

    @Operation(summary = "로그인", description = "사용자가 로그인 API입니다.")
    ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response);

    @Operation(summary = "accessToken 재발급", description = "서버 인증을 위한 accessToken 재발급을 위한 API입니다.")
    ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "아이디 찾기", description = "사용자의 아이디를 찾기 위한 API입니다.")
    ResponseEntity<String> findingId(@RequestParam("email") @Valid @Email String email);

    @Operation(summary = "비밀번호 찾기", description = "사용자의 비밀번호를 찾기 위한 API입니다. <br> 임시 비밀번호를 발급합니다.")
    ResponseEntity<String> findPass(@RequestParam("email") @Valid @Email String email,
                                    @RequestParam("username") String username);

    @Operation(summary = "사용자 권한 확인", description = "사용자의 권한을 확인하기 위한 API입니다.")
    ResponseEntity<UserRoleDto> getUserRole(@RequestHeader("Authorization") String token);
}
