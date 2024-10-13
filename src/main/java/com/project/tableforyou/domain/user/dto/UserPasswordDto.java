package com.project.tableforyou.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
@Schema(name = "UserPasswordDto", description = "사용자 비밀번호 업데이트 DTO")
public class UserPasswordDto {

    @Schema(description = "현재 비밀번호", example = "password")
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @Schema(description = "새로운 비밀번호", example = "newPassword")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,16}", message = "비밀번호는 8~16자 영문자, 숫자, 특수문자를 사용하세요.")
    private String newPassword;
}
