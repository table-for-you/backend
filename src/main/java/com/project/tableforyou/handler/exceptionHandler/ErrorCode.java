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

    // 존재하지 않는 값을 보낼 때.
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    RESTAURANT_NOT_FOUND(404, "존재하지 않는 가게입니다."),
    RESERVATION_NOT_FOUND(404, "해당하는 예약번호가 없습니다."),
    MENU_NOT_FOUND(404, "존재하지 않는 메뉴입니다."),

    // 서버 에러
    INTERNAL_SERVER_ERROR(500, "서버 에러입니다. 서버 팀에 연락주세요.");

    private final int status;
    private final String message;
}
