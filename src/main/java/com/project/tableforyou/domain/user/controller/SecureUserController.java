package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.like.service.LikeService;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.user.dto.PasswordDto;
import com.project.tableforyou.domain.user.dto.UserRequestDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.domain.user.service.UserService;
import com.project.tableforyou.handler.validate.ValidateHandler;
import com.project.tableforyou.security.auth.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final LikeService likeService;
    private final ValidateHandler validateHandler;
    

    /* 회원 불러오기 */
    @GetMapping("/{username}")
    public UserResponseDto read(@PathVariable(name = "username") String username) {
        return userService.findByUsername(username);
    }

    /* 회원 업데이트 */
    @PutMapping
    public ResponseEntity<String> update(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @RequestBody UserUpdateDto dto) {

        userService.update(principalDetails.getUsername(), dto);
        return ResponseEntity.ok("회원 업데이트 성공.");

    }

    /* 현재 비밀번호 검사 */
    @GetMapping("/check-password")
    public Object checkPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                @RequestBody PasswordDto passwordDto) {

        return userService.checkPass(principalDetails.getUsername(), passwordDto);
    }

    /* 회원 삭제 */
    @DeleteMapping
    public ResponseEntity<String> delete(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        userService.delete(principalDetails.getUsername());
        return ResponseEntity.ok("회원 삭제 성공.");
    }

    /* 좋아요한 가게 불러오기 */
    @GetMapping("/like-restaurants")
    public List<RestaurantNameDto> likeRestaurants(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return likeService.getLikeRestaurants(principalDetails.getUsername());
    }
}