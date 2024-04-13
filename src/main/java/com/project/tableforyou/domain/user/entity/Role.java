package com.project.tableforyou.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER("ROLE_USER"), OWNER("ROLE_OWNER"), ADMIN("ROLE_ADMIN");

    private final String value;
}
