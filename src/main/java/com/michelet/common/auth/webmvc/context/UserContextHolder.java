package com.michelet.common.auth.webmvc.context;

import com.michelet.common.auth.core.context.UserContext;

public final class UserContextHolder {

    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(UserContext userContext) {
        HOLDER.set(userContext);
    }

    public static UserContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
