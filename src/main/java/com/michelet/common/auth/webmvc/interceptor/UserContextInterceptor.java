package com.michelet.common.auth.webmvc.interceptor;

import com.michelet.common.auth.core.constants.AuthHeaders;
import com.michelet.common.auth.core.context.UserContext;
import com.michelet.common.auth.core.enums.UserRole;
import com.michelet.common.auth.webmvc.context.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        UserContextHolder.clear();

        String userId = normalize(request.getHeader(AuthHeaders.USER_ID));
        String roleValue = normalize(request.getHeader(AuthHeaders.USER_ROLE));


        if(userId!=null)
            UserContextHolder.set(new UserContext(userId,parseRole(roleValue)));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        UserContextHolder.clear();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private UserRole parseRole(String roleValue) {
        if (roleValue == null) {
            return null;
        }

        try {
            return UserRole.valueOf(roleValue);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
