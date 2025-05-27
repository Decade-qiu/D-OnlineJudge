package com.decade.doj.common.utils;

public class UserContext {

    private static final ThreadLocal<Long> userHolder = new ThreadLocal<>();

    public static Long getCurrentUser() {
        return userHolder.get();
    }

    public static void setCurrentUser(Long id) {
        userHolder.set(id);
    }

    public static void clear() {
        userHolder.remove();
    }

}
