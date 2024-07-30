package com.project.tableforyou.domain.user.apl;

import com.project.tableforyou.domain.user.dto.SignUpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[(권한 필요 x) 사용자 API]", description = "권한이 필요없는 사용자 관련 API")
public interface PublicUserApi {

    @Operation(summary = "회원가입", description = "서비스 최초 이용시 회원가입을 하는 API입니다.")
    ResponseEntity<?> register(@Valid @RequestBody SignUpDto signUpDto);

    @Operation(summary = "아이디 중복 확인하기", description = "회원가입 및 정보 업데이트 중, 중복된 아이디가 있는지 확인하는 API입니다.")
    ResponseEntity<?> checkUsernameExists(@RequestParam("username") String username);

    @Operation(summary = "닉네임 중복 확인하기", description = "회원가입 및 정보 업데이트 중, 중복된 닉네임이 있는지 확인하는 API입니다.")
    ResponseEntity<?> checkNicknameExists(@RequestParam("nickname") String nickname);
}
