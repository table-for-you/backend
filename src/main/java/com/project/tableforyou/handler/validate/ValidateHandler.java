package com.project.tableforyou.handler.validate;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component
public class ValidateHandler {

    /* 에러 확인 */
    public Map<String, String> validate(Errors errors) {
        Map<String, String> validateResult = new HashMap<>();

        for (FieldError error: errors.getFieldErrors()) {
            validateResult.put(error.getField(), error.getDefaultMessage());
        }
        return validateResult;
    }
}
