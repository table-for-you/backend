package com.project.tableforyou.jwt.handler;

import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.domain.dto.AuthDto;
import com.project.tableforyou.jwt.JwtUtil;
import com.project.tableforyou.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.project.tableforyou.jwt.JwtProperties.*;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(principalDetails);     // Access Token 발급
        String refreshToken = jwtUtil.generateRefreshToken(principalDetails);   // Refresh Token 발급

        String username = principalDetails.getUsername();

        if(authService.existsByUsername(username)) {
            authService.delete(username);
        }

        String refreshUUID = getRefreshUUID(refreshToken, username);

        response.addHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessToken);    // 헤더에 access Token 추가
        response.addCookie(createCookie(REFRESH_COOKIE_VALUE, refreshUUID));        // 쿠키에 refresh Token Index 값 추가.
        response.setStatus(HttpServletResponse.SC_OK);

    }
    /* Refresh Token db저장 및 key값 가져오기 */
    private String getRefreshUUID(String refreshToken, String username) {
        AuthDto authDTO = AuthDto.builder()
                .token(refreshToken)
                .username(username)
                .build();

        return authService.save(authDTO);
    }

    /* 쿠키 생성 */
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}