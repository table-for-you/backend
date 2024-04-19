package com.project.tableforyou.token.service;

import com.project.tableforyou.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final RedisUtil redisUtil;
    private final static String BLACKLIST_KEY_PREFIX = "BlackListToken:";
    private final static long BLACKLIST_EXPIRATION_TIME = 30*60;

    /* redis에 저장 */
    public void save(String accessToken) {

        String key = BLACKLIST_KEY_PREFIX + accessToken;
        redisUtil.set(key, accessToken);
        redisUtil.expire(key, BLACKLIST_EXPIRATION_TIME);
    }

    /* 블랙리스트 확인. */
    public boolean existsById(String accessToken) {
        return redisUtil.setExisted(BLACKLIST_KEY_PREFIX + accessToken);
    }

}
