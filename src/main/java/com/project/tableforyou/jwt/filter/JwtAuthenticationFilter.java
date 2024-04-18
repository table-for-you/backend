package com.project.tableforyou.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorDto;
import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.token.service.AccessTokenService;
import com.project.tableforyou.utils.jwt.JwtUtil;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.project.tableforyou.utils.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static com.project.tableforyou.utils.jwt.JwtProperties.TOKEN_PREFIX;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AccessTokenService accessTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessTokenGetHeader = request.getHeader(ACCESS_HEADER_VALUE);

        /* 로그인 되어 있지 않은 사용자 */
        if(accessTokenGetHeader == null || !accessTokenGetHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = accessTokenGetHeader.substring(TOKEN_PREFIX.length()).trim();

        if(accessTokenService.existsById(accessToken)) {       // AccessToken이 블랙리스트에 있는지.
            handleExceptionToken(response, ErrorCode.BLACKLIST_ACCESS_TOKEN);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);      // 만료되었는지
        } catch (ExpiredJwtException e) {
            handleExceptionToken(response, ErrorCode.ACCESS_TOKEN_EXPIRED);
            return;
        }

        if (!"access".equals(jwtUtil.getCategory(accessToken))) {         // jwt에 담긴 category를 통해 access 가 맞는지 확인.
            handleExceptionToken(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        Authentication auth = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    /* Authentication 가져오기 */
    private Authentication getAuthentication(String token) {

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        User userEntity = new User();
        userEntity.setUsername(username);
        userEntity.setRole(Role.valueOf(role));

        PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

        return new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
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