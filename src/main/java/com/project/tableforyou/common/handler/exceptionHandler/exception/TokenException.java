package com.project.tableforyou.common.handler.exceptionHandler.exception;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenException extends RuntimeException {

    private final ErrorCode errorCode;
}
