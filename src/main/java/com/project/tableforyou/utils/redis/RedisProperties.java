package com.project.tableforyou.utils.redis;

public interface RedisProperties {

    String BLACKLIST_KEY_PREFIX = "BlackListToken:";
    long BLACKLIST_EXPIRATION_TIME = 30*60;
    String REFRESH_TOKEN_KEY_PREFIX = "RefreshToken:";
    long REFRESH_EXPIRATION_TIME_IN_REDIS = 24*60*60;
    String RESERVATION_KEY_PREFIX = "reservation:";
}