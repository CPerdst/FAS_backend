package com.l1Akr.common.utils;

import com.l1Akr.dao.UserDAO;

public class UserThreadLocal {
    private static final ThreadLocal<UserDAO> user = new ThreadLocal<>();

    public static void setCurrentUser(UserDAO user) {
        UserThreadLocal.user.set(user);
    }

    public static UserDAO getCurrentUser() {
        return user.get();
    }

    public static void removeCurrentUser() {
        user.remove();
    }

}
