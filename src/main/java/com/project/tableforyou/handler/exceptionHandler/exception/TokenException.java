package com.project.tableforyou.handler.exceptionHandler.exception;

import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenException extends RuntimeException {

    private final ErrorCode errorCode;
}
