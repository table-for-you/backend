package com.project.tableforyou.domain.auth.dto;

import lombok.Getter;

@Getter
public class UserRoleDto {

    private final String role;

    public UserRoleDto(String role) {
        this.role = role;
    }
}
