package com.project.tableforyou.common.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorDto;
import com.project.tableforyou.security.token.service.TokenBlackListService;
import com.project.tableforyou.common.utils.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.project.tableforyou.common.utils.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static com.project.tableforyou.common.utils.jwt.JwtProperties.TOKEN_PREFIX;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final TokenBlackListService tokenBlackListService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        if ("/".equals(requestUri)) {       // index화면 넘기기.
            filterChain.doFilter(request, response);
            return;
        }
        String accessTokenGetHeader = request.getHeader(ACCESS_HEADER_VALUE);

        /* 로그인 되어 있지 않은 사용자 */
        if(accessTokenGetHeader == null || !accessTokenGetHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveAccessToken(response, accessTokenGetHeader);

        if (accessToken == null)    // resolveAccessToken 메서드에 의해 accessToken에 문제가 있을 경우.
            return;

        Authentication auth = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    /*
    * @throws accessToken이 blackList에 저장되어 있거나, 만료되었거나, access 토큰이 아니거나.
    */
    private String resolveAccessToken(HttpServletResponse response, String accessTokenGetHeader) throws IOException {
        String accessToken = accessTokenGetHeader.substring(TOKEN_PREFIX.length()).trim();

        if(tokenBlackListService.existsById(accessToken)) {       // AccessToken이 블랙리스트에 있는지.
            handleExceptionToken(response, ErrorCode.BLACKLIST_ACCESS_TOKEN);
            return null;
        }

        try {
            jwtUtil.isExpired(accessToken);      // 만료되었는지
        } catch (ExpiredJwtException e) {
            handleExceptionToken(response, ErrorCode.ACCESS_TOKEN_EXPIRED);
            return null;
        }
        
        return accessToken;
    }

    /* Authentication 가져오기 */
    private Authentication getAuthentication(String token) {

        String userId = String.valueOf(jwtUtil.getUserId(token));

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /* 예외 처리 */
    private void handleExceptionToken(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        ErrorDto error = new ErrorDto(errorCode.getStatus(), errorCode.getMessage());
        String messageBody = objectMapper.writeValueAsString(error);

        log.error("Error occurred: {}", error.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(messageBody);
    }
}