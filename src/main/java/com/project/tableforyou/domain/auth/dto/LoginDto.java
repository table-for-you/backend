package com.project.tableforyou.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(name = "LoginDto", description = "로그인 요청 DTO")
public class LoginDto {

    @Schema(description = "아이디", example = "test")
    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;
    @Schema(description = "비밀번호", example = "password")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}

