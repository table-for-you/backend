package com.project.tableforyou.common.handler.exceptionHandler;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorDto;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.common.handler.exceptionHandler.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* CustomException 처리 */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity customException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorDto error = new ErrorDto(errorCode.getStatus(), errorCode.getMessage());
        log.error("Error occurred: {}", error.getMessage());
        return new ResponseEntity(error, HttpStatus.valueOf(error.getStatus()));
    }

    /* TokenException 처리 */
    @ExceptionHandler(TokenException.class)
    protected ResponseEntity tokenException(TokenException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorDto error = new ErrorDto(errorCode.getStatus(), errorCode.getMessage());
        log.error("Error occurred: {}", error.getMessage());
        return new ResponseEntity(error, HttpStatus.valueOf(error.getStatus()));
    }

    /* 잘못된 입력 예외 처리 */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity customIllegalArgumentException() {
        ErrorDto error = new ErrorDto(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getMessage());
        return new ResponseEntity(error, HttpStatus.valueOf(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getStatus()));
    }

    /* 일반 예외 처리 */
    @ExceptionHandler
    protected ResponseEntity customServerException(Exception ex) {
        ErrorDto error = new ErrorDto(ErrorCode.INTERNAL_SERVER_ERROR.getStatus(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* MethodArgumentNotValidException 처리 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.error("Validation errors: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
