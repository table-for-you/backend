package com.project.tableforyou.common.aop;

import com.project.tableforyou.common.aop.annotation.RestaurantId;
import com.project.tableforyou.common.aop.annotation.ReviewId;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.domain.review.repository.ReviewRepository;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class VerifyAuthenticationAspect {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    @Around("@annotation(com.project.tableforyou.common.aop.annotation.VerifyAuthentication)")
    public Object verifyAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {

        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String expectedUsername = principalDetails.getUsername();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof RestaurantId) {
                    Long restaurantId = (Long) args[i];
                    restaurantVerifyAuthentication(expectedUsername, restaurantId);
                    break;
                } else if (annotation instanceof ReviewId) {
                    Long reviewId = (Long) args[i];
                    reviewVerifyAuthentication(expectedUsername, reviewId);
                    break;
                }
            }
        }

        log.info("Permission verified");
        return joinPoint.proceed();
    }

    /* 레스토랑에 대한 권환 확인 메서드 */
    private void restaurantVerifyAuthentication(String expectedUsername, Long restaurantId) {

        String actualUsername = restaurantRepository.findUsernameByRestaurantId(restaurantId);
        if (actualUsername == null) {
            throw new CustomException(ErrorCode.RESTAURANT_NOT_FOUND);
        }

        if (!expectedUsername.equals(actualUsername)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void reviewVerifyAuthentication(String expectedUsername, Long reviewId) {
        String actualUsername = reviewRepository.findUsernameByReviewId(reviewId);
        if (actualUsername == null) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }
        if (!expectedUsername.equals(actualUsername)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

}
