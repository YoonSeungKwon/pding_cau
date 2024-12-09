package yoon.capstone.application.common.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.UnauthorizedException;

@Aspect
@Component
public class AuthenticatedAspect {

    @Pointcut("@annotation(yoon.capstone.application.common.annotation.Authenticated)")
    public void authenticatedCheck(){};

    @Before("authenticatedCheck()")
    public void authenticationExistCheck(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); // 로그인되지 않았거나 만료됨
        }
    }

}
