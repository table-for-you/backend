package com.project.tableforyou.aop;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class VerifyAuthenticationAspect {

    private final RestaurantRepository restaurantRepository;

    @Around("@annotation(com.project.tableforyou.aop.annotation.VerifyAuthentication)")
    public Object verifyAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();

        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String expectedUsername = principalDetails.getUsername();

        for (Object arg : args) {
            if (arg instanceof Long) {
                Long restaurantId = (Long) arg;
                restaurantVerifyAuthentication(expectedUsername, restaurantId);
            } else if (arg instanceof UserUpdateDto) {
                UserUpdateDto userDto = (UserUpdateDto) arg;
                userVerifyAuthentication(expectedUsername, userDto);
            }
        }

        return joinPoint.proceed();
    }

    private void restaurantVerifyAuthentication(String expectedUsername, Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (!expectedUsername.equals(restaurant.getUser().getUsername()))
            throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    private void userVerifyAuthentication(String expectedUsername, UserUpdateDto userDto) {

        if(!expectedUsername.equals(userDto.getUsername()))
            throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

}
