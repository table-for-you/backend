package com.project.tableforyou.redis.service;

import com.project.tableforyou.handler.exceptionHandler.CustomException;
import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import com.project.tableforyou.redis.domain.RefreshToken;
import com.project.tableforyou.redis.domain.RefreshTokenDto;
import com.project.tableforyou.redis.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /* redis에 저장 */
    @Transactional
    public void save(RefreshTokenDto refreshTokenDto) {

        RefreshToken refreshToken = refreshTokenDto.toEntity();
        refreshTokenRepository.save(refreshToken);
    }

    /* refreshToken으로 redis에서 불러오기 */
    @Transactional(readOnly = true)
    public RefreshTokenDto findByRefreshToken(String refreshToken) {

        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new CustomException(ErrorCode.REFRESHTOKEN_NOT_FOUND));

        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .username(findRefreshToken.getUsername())
                .refreshToken(findRefreshToken.getRefreshToken())
                .build();
        return refreshTokenDto;
    }

    /* redis에서 삭제 */
    @Transactional
    public void delete(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new CustomException(ErrorCode.REFRESHTOKEN_NOT_FOUND));

        refreshTokenRepository.delete(findRefreshToken);
    }

}
