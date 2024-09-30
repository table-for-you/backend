package com.project.tableforyou.security.token.service;

import com.project.tableforyou.common.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.project.tableforyou.common.utils.redis.RedisProperties.BLACKLIST_EXPIRATION_TIME;
import static com.project.tableforyou.common.utils.redis.RedisProperties.BLACKLIST_KEY_PREFIX;


@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final RedisUtil redisUtil;

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
