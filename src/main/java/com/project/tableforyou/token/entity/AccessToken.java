package com.project.tableforyou.token.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "accessToken", timeToLive = 30*60)
public class AccessToken {

    @Id
    private String accessToken;
}
