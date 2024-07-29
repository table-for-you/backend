package com.project.tableforyou.token.service;

import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.TokenException;
import com.project.tableforyou.utils.jwt.JwtUtil;
import com.project.tableforyou.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.project.tableforyou.utils.redis.RedisProperties.REFRESH_EXPIRATION_TIME_IN_REDIS;
import static com.project.tableforyou.utils.redis.RedisProperties.REFRESH_TOKEN_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    /* redis에 저장 */
    public void save(String username, String refreshToken) {

        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        redisUtil.set(key, refreshToken);
        redisUtil.expire(key, REFRESH_EXPIRATION_TIME_IN_REDIS);
    }

    /* refreshToken으로 redis에서 불러오기 */
    public String findByRefreshToken(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);
        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        String findRefreshToken = (String) redisUtil.get(key);
        if(findRefreshToken == null)
            throw new TokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);

        return findRefreshToken;
    }

    /* redis에서 삭제 */
    public void delete(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);
        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        redisUtil.del(key);
    }

    /* accessToken 재발급 */
    public String accessTokenReIssue(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        Long userId = jwtUtil.getUserId(refreshToken);

        return jwtUtil.generateAccessToken(role, username, userId);      // 재발급
    }

    /* Refresh token rotation(RTR) 사용 */
    public String refreshTokenReIssue(String refreshToken) {

        this.delete(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        Long userId = jwtUtil.getUserId(refreshToken);

        String refreshTokenReIssue = jwtUtil.generateRefreshToken(role, username, userId);

        this.save(username, refreshTokenReIssue);

        return refreshTokenReIssue;
    }

}
