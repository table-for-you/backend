package com.project.tableforyou.common.handler.exceptionHandler.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDto {

    private final int status;
    private final String message;
}
