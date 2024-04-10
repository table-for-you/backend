package com.project.tableforyou.handler.exceptionHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 올바르지 않은 파라미터 값을 보낼 때.
    INVALID_PARAMETER(400, "올바른 값을 입력해주세요."),

    // 인증이 되어 있지 않을 때.
    UNAUTHORIZED(401, "접근 권한이 없습니다."),
    ACCESS_TOKEN_EXPIRED(401, "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(401, "Access Token이 잘못되었습니다."),

    // RefreshToken 인증 중 오류
    REFRESH_TOKEN_NOT_FOUND(404, "RefreshToken이 존재하지 않습니다."),
    REFRESG_TOKEN_EXPIRED(404, "RefreshToken이 만료되었습니다."),

    // 존재하지 않는 값을 보낼 때.
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    RESTAURANT_NOT_FOUND(404, "존재하지 않는 가게입니다."),
    RESERVATION_NOT_FOUND(404, "해당하는 예약번호가 없습니다."),
    MENU_NOT_FOUND(404, "존재하지 않는 메뉴입니다."),
    USER_INVALID_PASSWORD(404, "잘못된 비밀번호입니다."),

    // 계정관련 오류
    USER_LOCKED(404, "계정이 잠겨있습니다. 5분 후 다시 시도해 주세요."),

    // 서버 에러
    INTERNAL_SERVER_ERROR(500, "서버 에러입니다. 서버 팀에 연락주세요.");

    private final int status;
    private final String message;
}
