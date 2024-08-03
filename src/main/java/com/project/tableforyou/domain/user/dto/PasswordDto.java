package com.project.tableforyou.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(name = "PasswordDto", description = "비밀번호 요청 DTO")
public class PasswordDto {
    @Schema(description = "비밀번호", example = "password")
    private String password;
}
