package com.l1Akr.common.util;

import com.l1Akr.pojo.dto.InnerUserInfo;

public class UserThreadLocal {

    private static final ThreadLocal<InnerUserInfo> user = new ThreadLocal<>();

    public static void setCurrentUser(InnerUserInfo user) {
        UserThreadLocal.user.set(user);
    }

    public static InnerUserInfo getCurrentUser() {
        return user.get();
    }

    public static void removeCurrentUser() {
        user.remove();
    }

}
