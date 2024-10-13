package com.project.tableforyou.common.handler.exceptionHandler.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 올바르지 않은 파라미터 값을 보낼 때.
    INVALID_PARAMETER(400, "올바른 값을 입력해주세요."),
    INVALID_USERNAME_PATTERN(400, "아이디는 특수문자를 제외한 4~20자리여야 합니다."),
    INVALID_NICKNAME_PATTERN(400, "닉네임은 특수문자를 제외한 2~10자리여야 합니다."),
    ILLEGAL_ARGUMENT_EXCEPTION(400, "데이터가 없습니다."),
    INVALID_CURRENT_PASSWORD(400, "현재 비밀번호를 잘못 입력하였습니다."),

    // 인증이 되어 있지 않을 때.
    UNAUTHORIZED(401, "접근 권한이 없습니다."),
    ACCESS_TOKEN_EXPIRED(401, "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(401, "Access Token이 잘못되었습니다."),

    // RefreshToken 인증 중 오류
    REFRESH_TOKEN_NOT_FOUND(404, "RefreshToken이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(404, "RefreshToken이 만료되었습니다."),

    // AccessToken 관련 오류
    BLACKLIST_ACCESS_TOKEN(403, "접근 불가한 AccessToken입니다."),

    TOKEN_NOT_FOUND(404, "Token이 존재하지 않습니다."),

    // 존재하지 않는 값을 보낼 때.
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    RESTAURANT_NOT_FOUND(404, "존재하지 않는 가게입니다."),
    RESERVATION_NOT_FOUND(404, "해당하는 예약번호가 없습니다."),
    MENU_NOT_FOUND(404, "존재하지 않는 메뉴입니다."),
    LIKE_NOT_FOUND(404, "해당 가게에 좋아요를 누른 적이 없습니다."),
    NOTIFICATION_NOT_FOUND(404, "해당 알림이 존재하지 않습니다."),

    // 로그인 과정 에러.
    USER_INVALID_PASSWORD(404, "잘못된 비밀번호입니다."),
    INVALID_USER_INFO(404, "정보를 정확히 입력해주세요."),

    // 이미 존재하는 값을 보냈을 때.
    ALREADY_LIKE_RESTAURANT(409, "이미 해당 가게를 좋아요 하였습니다."),
    ALREADY_USER_RESERVATION(409, "이미 해당 가게에 예약을 하였습니다."),
    ALREADY_EXISTS_MAIL(409, "이미 존재하는 이메일입니다."),
    NO_AVAILABLE_SEATS(409, "해당 시간대에 예약 가능한 좌석이 없습니다."),

    // 계정관련 오류
    USER_LOCKED(404, "계정이 잠겨있습니다. 5분 후 다시 시도해 주세요."),

    // 인증 메일 관련 오류
    ALREADY_MAIL_REQUEST(429, "1분 후 재전송 해주세요."),
    INVALID_MAIL_ADDRESS(404, "잘못된 이메일입니다."),
    CODE_EXPIRED(410, "유효시간이 지났습니다."),
    INVALID_CODE(400, "인증번호가 일치하지 않습니다."),



    // 서버 에러
    LOCK_ACQUISITION_ERROR(503, "락 획득 실패"),
    THREAD_INTERRUPTED(500, "스레드가 중단되었습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 에러입니다. 서버 팀에 연락주세요.");

    private final int status;
    private final String message;
}
