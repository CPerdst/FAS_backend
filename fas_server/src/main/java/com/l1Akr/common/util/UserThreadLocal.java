package com.l1Akr.common.util;

import com.l1Akr.pojo.po.UserBasePO;

public class UserThreadLocal {
    private static final ThreadLocal<UserBasePO> user = new ThreadLocal<>();

    public static void setCurrentUser(UserBasePO user) {
        UserThreadLocal.user.set(user);
    }

    public static UserBasePO getCurrentUser() {
        return user.get();
    }

    public static void removeCurrentUser() {
        user.remove();
    }

}
