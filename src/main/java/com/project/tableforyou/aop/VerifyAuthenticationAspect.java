package com.project.tableforyou.aop;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
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
                break;      // 파라미터가 순서대로 들어가므로 MenuService의 update같이 타입이 같은게 있을 경우를 대비해 retaurantId만 받고 break한다.
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

}
