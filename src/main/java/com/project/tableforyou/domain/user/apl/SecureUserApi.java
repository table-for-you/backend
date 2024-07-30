package com.project.tableforyou.domain.user.apl;

import com.project.tableforyou.domain.user.dto.PasswordDto;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[(권한 필요 o) 사용자 API]", description = "권한이 필요한 사용자 관련 API")
public interface SecureUserApi {

    @Operation(summary = "자신의 정보 불러오기 *", description = "로그인된 사용자의 정보를 불러오는 API입니다.")
    ResponseEntity<?> readUser(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "현재 비밀번호 검사하기 *", description = "로그인된 사용자의 현재 비밀번호를 검사하는 API입니다." +
                                                            "<br>회원 정보 수정시, 사용될 수 있습니다.")
    ResponseEntity<?> checkPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                    @RequestBody PasswordDto passwordDto);

    @Operation(summary = "회원 정보 수정하기 *", description = "로그인된 사용자의 정보를 수정하는 API입니다.")
    ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                 @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "회원 정보 삭제하기 *", description = "로그인된 사용자의 정보를 삭제하는 API입니다.")
    ResponseEntity<?> deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "좋아요한 가게 불러오기 *", description = "로그인된 사용자가 좋아요한 가게를 불러오는 API입니다.")
    ResponseEntity<?> getRestaurantLike(@AuthenticationPrincipal PrincipalDetails principalDetails);
}
