package com.l1Akr.interceptor;

import com.l1Akr.pojo.dto.InnerUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RoleValidator extends PermissionValidator {
    @Override
    public boolean validate(InnerUserInfo userInfo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 这里可以添加具体的角色验证逻辑
        // 例如检查用户是否拥有访问该接口所需的角色
        return checkNext(userInfo, request, response);
    }
}