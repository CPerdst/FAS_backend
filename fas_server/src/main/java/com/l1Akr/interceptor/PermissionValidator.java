package com.l1Akr.interceptor;

import com.l1Akr.pojo.dto.InnerUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class PermissionValidator {
    private PermissionValidator next;
    
    public PermissionValidator linkWith(PermissionValidator next) {
        this.next = next;
        return next;
    }
    
    public abstract boolean validate(InnerUserInfo userInfo, HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    protected boolean checkNext(InnerUserInfo userInfo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (next == null) {
            return true;
        }
        return next.validate(userInfo, request, response);
    }
}