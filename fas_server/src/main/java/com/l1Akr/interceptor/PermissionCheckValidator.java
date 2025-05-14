package com.l1Akr.interceptor;

import com.l1Akr.pojo.dto.InnerUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionCheckValidator extends PermissionValidator {
    @Override
    public boolean validate(InnerUserInfo userInfo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 这里可以添加具体的权限验证逻辑
        // 例如检查用户是否拥有访问该接口所需的权限
        return checkNext(userInfo, request, response);
    }
}