package com.project.tableforyou.handler.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RefreshTokenException extends RuntimeException {

    private final ErrorCode errorCode;
}
