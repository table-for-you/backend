package com.project.tableforyou.refreshToken.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 24*60*60)
public class RefreshToken {

    @Id
    private String username;        // username을 @Id로 한 이유는
                                    // 로그아웃(redis에서 refreshToken삭제)를 안한 상태에서 로그인을 하면 중복된 값을 지우기 위해.
                                    // refreshToken으로 한다면, 값이 다 다르기에 redis에서 삭제가 안됨.

    @Indexed                // secondary Index 지정.
    private String refreshToken;

}
