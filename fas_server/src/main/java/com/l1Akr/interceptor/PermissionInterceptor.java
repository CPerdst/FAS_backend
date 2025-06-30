package com.l1Akr.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.l1Akr.common.util.JwtUtils;
import com.l1Akr.pojo.po.UserBasePO;
import com.l1Akr.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;

@Component
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("PermissionInterceptor");
        // 1. 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            sendUnauthorized(response, "未提供有效的 Token");
            return false;
        }
        token = token.substring(7);

        try {
            // 2. 验证并解析token
            DecodedJWT decodedJWT = jwtUtils.validateAndParse(token);
            String userId = decodedJWT.getSubject();

            // 3. 查询用户信息并设置到线程本地
            UserBasePO user = userService.getUserById(userId);
            if (user == null) {
                sendUnauthorized(response, "用户不存在或已被禁用");
                return false;
            }
            // UserThreadLocal.setCurrentUser(user);

            // 4. 获取请求URI和权限标识
            String requestURI = request.getRequestURI();
            String permission = getPermissionFromURI(requestURI);
            
            // 5. 检查用户是否有该权限
            if (!hasPermission(userId, permission)) {
                sendUnauthorized(response, "用户没有访问权限");
                return false;
            }

            // 6. 检查token是否即将过期
            if (jwtUtils.isTokenExpireSoon(token)) {
                String newToken = jwtUtils.generateToken(userId, new HashMap<>());
                response.setHeader("New-Token", newToken);
            }

            return true;
        } catch (RuntimeException e) {
            sendUnauthorized(response, "Token 无效或已过期");
            return false;
        }
    }
    
    private String getPermissionFromURI(String uri) {
        if (uri.startsWith("/user/info")) {
            return "user:select";
        } else if (uri.startsWith("/user/update")) {
            return "user:update";
        } else if (uri.startsWith("/user/register")) {
            return "user:insert";
        }
        return "";
    }
    
    private boolean hasPermission(String userId, String permission) {
        return userService.hasPermission(userId, permission);
    }

    private void sendUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + msg + "\"}");
    }
}