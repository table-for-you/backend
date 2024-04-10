package com.project.tableforyou.domain.user.dto;

import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.security.oauth.provider.OAuth2UserInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String nickname;
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$", message = "아이디는 특수문자를 제외한 4~20자리여야 합니다.")
    private String username;
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
    @NotBlank(message = "나이는 필수 입력 값입니다.")
    private String age;
    private String provider;
    private String providerId;
    private Role role;

    /* dto -> Entity */
    public User toEntity() {
        return User.builder()
                .name(name)
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
                .name(oAuth2UserInfo.getName())
                .nickname(oAuth2UserInfo.getNickname())
                .username(oAuth2UserInfo.getUsername())
                .password(bCryptPasswordEncoder.encode(oAuth2UserInfo.getName()))
                .email(oAuth2UserInfo.getEmail())
                .role(Role.USER)
                .build();
    }
}
