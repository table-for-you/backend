package com.project.tableforyou.handler.exceptionHandler;

import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
}
