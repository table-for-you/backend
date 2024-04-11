package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.like.service.LikeService;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.user.dto.UserRequestDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.domain.user.service.UserService;
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
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final LikeService likeService;

    /* 회원가입 과정 */
    @PostMapping("/joinProc")
    public ResponseEntity<Object> joinProc(@Valid @RequestBody UserRequestDto dto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = userService.validateHandler(bindingResult);
                log.info("Failed to sign up: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            userService.create(dto);
            return ResponseEntity.ok("회원가입 성공.");
        } catch (Exception e) {
            log.error("Error occurred during sign up: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during sign up");
        }

    }

    /* 회원 불러오기 */
    @GetMapping("/{username}")
    public UserResponseDto read(@PathVariable(name = "username") String username) {
        return userService.findByUsername(username);
    }

    /* 회원 업데이트 */
    @PutMapping("/update")
    public ResponseEntity<String> update(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @RequestBody UserUpdateDto dto) {

        userService.update(principalDetails.getUsername(), dto);
        return ResponseEntity.ok("회원 업데이트 성공.");

    }

    /* 회원 삭제 */
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        userService.delete(principalDetails.getUsername());
        return ResponseEntity.ok("회원 삭제 성공.");
    }

    /* 아이디 중복 확인 */
    @GetMapping("/checkUsername")
    public Object checkUsernameExists(@RequestParam("username") String username) {
        return userService.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    @GetMapping("/checkNickname")
    public Object checkNicknameExists(@RequestParam("nickname") String nickname) {
        return userService.existsByNickname(nickname);
    }

    /* 좋아요한 가게 불러오기 */
    @GetMapping("/likeRestaurants")
    public List<RestaurantNameDto> likeRestaurants(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return likeService.getLikeRestaurants(principalDetails.getUsername());
    }
}