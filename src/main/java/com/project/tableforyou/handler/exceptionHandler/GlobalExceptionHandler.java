package com.project.tableforyou.handler.exceptionHandler;

import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorDto;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.exception.RefreshTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.project.tableforyou.handler.exceptionHandler.error.ErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity customException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorDto error = new ErrorDto(errorCode.getStatus(), errorCode.getMessage());
        log.error("Error occurred: {}", error.getMessage());
        return new ResponseEntity(error, HttpStatus.valueOf(error.getStatus()));
    }

    @ExceptionHandler(RefreshTokenException.class)
    protected ResponseEntity refreshTokenException(RefreshTokenException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorDto error = new ErrorDto(errorCode.getStatus(), errorCode.getMessage());
        log.error("Error occurred: {}", error.getMessage());
        return new ResponseEntity(error, HttpStatus.valueOf(error.getStatus()));
    }

    @ExceptionHandler
    protected ResponseEntity customServerException(Exception ex) {
        ErrorDto error = new ErrorDto(INTERNAL_SERVER_ERROR.getStatus(), INTERNAL_SERVER_ERROR.getMessage());
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
