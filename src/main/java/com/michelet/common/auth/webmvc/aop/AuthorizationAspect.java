package com.michelet.common.auth.webmvc.aop;

import com.michelet.common.auth.core.annotation.RequireRole;
import com.michelet.common.auth.core.context.UserContext;
import com.michelet.common.auth.webmvc.context.UserContextHolder;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Aspect
public class AuthorizationAspect {
    @Around("@within(com.michelet.common.auth.core.annotation.RequireRole) ||" +
            "@annotation(com.michelet.common.auth.core.annotation.RequireRole)")
    public Object authorize(ProceedingJoinPoint joinPoint) throws Throwable {
        RequireRole requireRole = resolveRequireRole(joinPoint);
        if (requireRole == null) {
            return joinPoint.proceed();
        }

        UserContext userContext = UserContextHolder.get();
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication context is missing.");
        }

        boolean authorized = Arrays.stream(requireRole.value())
                .anyMatch(userContext::hasRole);

        if (!authorized) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }

        return joinPoint.proceed();
    }

    private RequireRole resolveRequireRole(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method interfaceMethod = signature.getMethod();
        Method targetMethod = AopUtils.getMostSpecificMethod(
                signature.getMethod(),
                joinPoint.getTarget().getClass()
        );

        RequireRole targetMethodAnnotation =
                AnnotatedElementUtils.findMergedAnnotation(targetMethod, RequireRole.class);
        if (targetMethodAnnotation != null) {
            return targetMethodAnnotation;
        }

        RequireRole interfaceMethodAnnotation =
                AnnotatedElementUtils.findMergedAnnotation(interfaceMethod, RequireRole.class);
        if (interfaceMethodAnnotation != null) {
            return interfaceMethodAnnotation;
        }

        return AnnotatedElementUtils.findMergedAnnotation(
                joinPoint.getTarget().getClass(),
                RequireRole.class
        );
    }

}
