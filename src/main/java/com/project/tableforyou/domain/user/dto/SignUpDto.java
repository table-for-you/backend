package com.project.tableforyou.domain.user.dto;

import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.security.oauth.provider.OAuth2UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "SignUpDto", description = "회원가입 요청 DTO")
public class SignUpDto {

    @Schema(description = "닉네임", example = "테스터")
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String nickname;
    @Schema(description = "아이디", example = "test")
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$", message = "아이디는 특수문자를 제외한 4~20자리여야 합니다.")
    private String username;
    @Schema(description = "비밀번호", example = "password")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,16}", message = "비밀번호는 8~16자 영문자, 숫자, 특수문자를 사용하세요.")
    private String password;
    @Schema(description = "이메일", example = "test@naver.com")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
    @Schema(description = "나이", example = "20")
    @NotBlank(message = "나이는 필수 입력 값입니다.")
    private String age;
    @Schema(description = "프로바이더(사용x)", example = "naver")
    private String provider;
    @Schema(description = "프로바이더id(사용x)", example = "asd123")
    private String providerId;
    @Schema(description = "역할(USER, OWNER)", example = "OWNER")
    private Role role;

    /* dto -> Entity */
    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .username(username)
                .password(password)
                .email(email)
                .age(age)
                .role(role)
                .build();
    }

    public User toEntity(OAuth2UserInfo oAuth2UserInfo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .nickname(oAuth2UserInfo.getNickname())
                .username(oAuth2UserInfo.getUsername())
                .password(bCryptPasswordEncoder.encode(oAuth2UserInfo.getName()))
                .email(oAuth2UserInfo.getEmail())
                .role(Role.USER)
                .build();
    }
}
