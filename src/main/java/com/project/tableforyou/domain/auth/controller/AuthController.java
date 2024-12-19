package com.project.tableforyou.domain.auth.controller;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import com.project.tableforyou.domain.auth.api.AuthApi;
import com.project.tableforyou.domain.auth.dto.LoginDto;
import com.project.tableforyou.domain.auth.service.AuthService;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.common.handler.exceptionHandler.exception.TokenException;
import com.project.tableforyou.security.token.service.RefreshTokenService;
import com.project.tableforyou.common.utils.api.ApiUtil;
import com.project.tableforyou.common.utils.cookie.CookieUtil;
import com.project.tableforyou.common.utils.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.project.tableforyou.common.utils.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static com.project.tableforyou.common.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;
import static com.project.tableforyou.common.utils.jwt.JwtProperties.TOKEN_PREFIX;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@ApiGroup("Test")
public class AuthController implements AuthApi {
    
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    /* 로그인 */
    @Override
    @PostMapping("/login")
    @TrackApi(description = "test1")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {

        User user = authService.login(loginDto);

        String role = String.valueOf(user.getRole());

        String accessToken = TOKEN_PREFIX + jwtUtil.generateAccessToken(role, user.getUsername(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(role, user.getUsername(), user.getId());

        refreshTokenService.save(user.getUsername(), refreshToken);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("nickname", user.getNickname());
        responseData.put("accessToken", accessToken);

        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshToken).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(responseData);
    }

    /* accessToken 재발급 */
    @Override
    @PostMapping("/reissue")
    @TrackApi(description = "test2")
    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenInCookie = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);

        if (refreshTokenInCookie == null) {     // 쿠키에 Refresh Token이 없다면
            throw new TokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String refreshToken = refreshTokenService.findByRefreshToken(refreshTokenInCookie);

        if (jwtUtil.isExpired(refreshToken)) {    // refresh token 만료
            throw new TokenException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String accessTokenReIssue = refreshTokenService.accessTokenReIssue(refreshToken);

        // Refresh token rotation(RTR) 사용
        String refreshTokenReIssue = refreshTokenService.refreshTokenReIssue(refreshToken);

        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshTokenReIssue).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", TOKEN_PREFIX + accessTokenReIssue);

        return ResponseEntity.ok(responseData);
    }

    /* 로그아웃 */
    @Override
    @GetMapping("/logout")
    @TrackApi(description = "test3")
    public ResponseEntity<?> logout(@RequestHeader(value = ACCESS_HEADER_VALUE, required = false) String accessToken,
                                     @CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken,
                                     HttpServletResponse response) {

        if (accessToken == null || refreshToken == null) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        authService.signOut(accessToken, refreshToken, response);

        return ResponseEntity.ok(ApiUtil.from("로그아웃 되었습니다."));
    }


    /* 아이디 찾기 */
    @Override
    @GetMapping("/find-id")
    @TrackApi(description = "test4")
    public ResponseEntity<?> findingId(@RequestParam("email") @Valid @Email String email) {

        return ResponseEntity.ok(ApiUtil.from(authService.findingId(email)));
    }

    /* 비밀번호 찾기 */
    @Override
    @PostMapping("/find-pass")
    @TrackApi(description = "test5")
    public ResponseEntity<?> findPass(@RequestParam("email") @Valid @Email String email,
                                           @RequestParam("username") String username) {

        authService.findingPassword(username, email);
        return ResponseEntity.ok(ApiUtil.from("잠시 후 등록하신 메일로 임시 비밀번호가 도착합니다."));
    }

    /* 사용자 권한 확인 */
    @Override
    @GetMapping("/user-role")
    @TrackApi(description = "test6")
    public ResponseEntity<?> getUserRole(@RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(ApiUtil.from(authService.findUserRoleByToken(token)));
    }

}
