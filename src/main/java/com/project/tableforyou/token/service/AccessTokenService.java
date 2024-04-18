package com.project.tableforyou.token.service;

import com.project.tableforyou.token.entity.AccessToken;
import com.project.tableforyou.token.repository.AccessTokenRepository;
import com.project.tableforyou.utils.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final JwtUtil jwtUtil;

    /* redis에 저장 */
    @Transactional
    public void save(String accessToken) {

        AccessToken accessTokenObject = AccessToken.builder()
                .accessToken(accessToken)
                .build();
        accessTokenRepository.save(accessTokenObject);
    }

    /* 블랙리스트 확인. */
    @Transactional(readOnly = true)
    public boolean existsById(String accessToken) {

        return accessTokenRepository.existsById(accessToken);
    }

}
