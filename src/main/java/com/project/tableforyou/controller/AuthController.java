package com.project.tableforyou.controller;

import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.domain.entity.Auth;
import com.project.tableforyou.jwt.JwtUtil;
import com.project.tableforyou.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.project.tableforyou.jwt.JwtProperties.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        UUID refreshUUID = getRefreshUUID(request);

        if(refreshUUID == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refresh token null");
        }

        Auth refreshToken = authService.findRefreshToken(refreshUUID);

        if(refreshToken.isRevoked()) {      // 로그아웃 되어 사용 불가능한 refresh token 인지.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refresh token Revoked");
        }

        PrincipalDetails principalDetails = authService.getUserDetails(refreshToken);

        if(!authService.isRefreshTokenValid(refreshToken, principalDetails)) {  // refresh token이 맞는지 && 만료 되었는지 && 회원것이 맞는지
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }

        String accessTokenReIssue = jwtUtil.generateAccessToken(principalDetails);      // 재발급

        response.setHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessTokenReIssue);

        return ResponseEntity.ok("Access Token reIssue");
    }

    private UUID getRefreshUUID(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        UUID refreshUUID = null;
        for(Cookie cookie: cookies) {
            if(cookie.getName().equals(REFRESH_COOKIE_VALUE))
                refreshUUID = UUID.fromString(cookie.getValue());
        }
        return refreshUUID;
    }
}
