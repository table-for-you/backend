package com.project.tableforyou.redis.serrvice;

import com.project.tableforyou.handler.exceptionHandler.CustomException;
import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import com.project.tableforyou.redis.RefreshToken;
import com.project.tableforyou.redis.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save((refreshToken));
    }

    @Transactional(readOnly = true)
    public RefreshToken findByRefreshToken(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new CustomException(ErrorCode.REFRESHTOKEN_NOT_FOUND));
        return findRefreshToken;
    }

    @Transactional
    public void delete(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new CustomException(ErrorCode.REFRESHTOKEN_NOT_FOUND));

        refreshTokenRepository.delete(findRefreshToken);
    }
}
