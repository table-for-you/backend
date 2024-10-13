package com.project.tableforyou.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(name = "UserUpdateDto", description = "사용자 업데이트 DTO")
public class UserUpdateDto {

    @Schema(description = "닉네임", example = "테스터")
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String nickname;

    @Schema(description = "나이", example = "20")
    private String age;
}
