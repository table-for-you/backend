package com.project.tableforyou.service;

import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.domain.dto.AuthDto;
import com.project.tableforyou.domain.entity.Auth;
import com.project.tableforyou.jwt.JwtUtil;
import com.project.tableforyou.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;

    /* Refresh Token 저장 */
    @Transactional
    public String save(AuthDto dto) {

        Auth auth = dto.toEntity();

        authRepository.save(auth);

        return String.valueOf(auth.getId());
    }

    /* Refresh Token 찾기 */
    @Transactional(readOnly = true)
    public Auth findRefreshToken(UUID refreshUUID) {

        Auth auth = authRepository.findById(refreshUUID).orElseThrow(() ->
                new IllegalArgumentException("Refresh Token이 없습니다."));

        return auth;
    }

    /* 로그아웃된 Refresh Token 사용 불가 만들기 */
    @Transactional
    public void revokedRefreshByLogout(UUID refreshUUID) {
        Auth auth = authRepository.findById(refreshUUID).orElseThrow(() ->
                new IllegalArgumentException("Refresh Token이 없습니다."));
        auth.setRevoked(true);
    }

    /* Refresh Token 삭제 */
    @Transactional
    public void delete(String username) {

        Auth auth = authRepository.findByUsername(username);
        authRepository.delete(auth);
    }

    /* username의 Refresh Token이 이미 존재하는지*/
    public boolean existsByUsername(String username) {
        return authRepository.existsByUsername(username);
    }

    /* 유효성 검사 */
    public boolean isRefreshTokenValid(Auth refreshToken, PrincipalDetails principalDetails) {
        String category = jwtUtil.getCategory(refreshToken.getToken());
        return "refresh".equals(category) && jwtUtil.validateToken(refreshToken.getToken(), principalDetails);
    }

    /* UserDetails 가져오기 */
    public PrincipalDetails getUserDetails(Auth refreshToken) {
        String username = jwtUtil.getUsername(refreshToken.getToken());
        return jwtUtil.getUserDetails(username);

    }
}