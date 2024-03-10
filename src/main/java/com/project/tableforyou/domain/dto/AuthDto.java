package com.project.tableforyou.domain.dto;

import com.project.tableforyou.domain.entity.Auth;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthDto {

    private String username;
    private String token;

    public Auth toEntity() {
        return Auth.builder()
                .username(username)
                .token(token)
                .revoked(false)
                .build();
    }
}