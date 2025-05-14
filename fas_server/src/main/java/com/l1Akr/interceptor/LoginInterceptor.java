package com.l1Akr.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.l1Akr.annotation.RequiredPermission;
import com.l1Akr.common.util.JwtUtils;
import com.l1Akr.common.util.UserThreadLocal;
import com.l1Akr.pojo.dto.InnerUserInfo;
import com.l1Akr.pojo.po.PermissionPO;
import com.l1Akr.pojo.po.RolePO;
import com.l1Akr.pojo.po.UserBasePO;
import com.l1Akr.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        log.debug("Interceptor:  {}", request.getRequestURI());

        if(!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();

        // 2. 获取方法上的权限注解
        RequiredPermission permissionAnnotation = method.getAnnotation(RequiredPermission.class);

        // 3. 没有权限校验注解的接口一律不能访问
        if (permissionAnnotation == null || (permissionAnnotation.permissions().length == 0 && permissionAnnotation.roles().length == 0)) {
            sendUnauthorized(response, "接口无法访问");
            return false;
        }

        // 1. 从 Header 中获取 Token
        String token = request.getHeader("Authorization");
        if(token == null || !token.startsWith("Bearer ")) {
            sendUnauthorized(response, "未提供有效的 Token");
            return false;
        }
        token = token.substring(7);

        try {
            DecodedJWT decodedJWT = jwtUtils.validateAndParse(token);

            String userId = decodedJWT.getSubject();
            Map<String, Claim> claims = decodedJWT.getClaims();
            List<RolePO> roles = jwtUtils.parseListClaim(claims.get("roles"), RolePO.class);
            List<PermissionPO> permissions = jwtUtils.parseListClaim(claims.get("permissions"), PermissionPO.class);

            // 校验用户角色
            if(Arrays.stream(permissionAnnotation.roles())
                    .noneMatch(requiredAuth -> roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(requiredAuth))) &&
                    Arrays.stream(permissionAnnotation.permissions())
                    .noneMatch(requiredAuth -> permissions.stream().anyMatch(permission -> permission.getPermissionName().equalsIgnoreCase(requiredAuth)))) {
                // 如果该用户没有该接口所需角色权限，同时也没有该接口所需权限
                sendUnauthorized(response, "您没有权限访问此接口");
                return false;
            }

            // 查询用户是否存在，防止token有效但是用户被删除
            UserBasePO userById = userService.getUserById(userId);
            if(userById == null) {
                sendUnauthorized(response, "用户不存在或已被禁用");
                return false;
            }
            // 将用户存入ThreadLocal
            InnerUserInfo innerUserInfo = new InnerUserInfo();
            innerUserInfo.setUserBase(userById);
            innerUserInfo.setRoles(roles);
            innerUserInfo.setPermissions(permissions);
            UserThreadLocal.setCurrentUser(innerUserInfo);

            // 判断是否快要过期
            if (jwtUtils.isTokenExpireSoon(token)){
                Map<String, Object> map = new HashMap<>();
                String newToken = jwtUtils.generateToken(userId, map);
                response.setHeader("New-Token", newToken);
                log.info("Token即将过期，刷新用户{}的Token: {}", userId, newToken);
            }

            // 进行权限验证
            // if (!permissionValidatorChain.validate(innerUserInfo, request, response)) {
            //     return false;
            // }
            
            // 否则将用户信息存入LocalStorage
            return true;
        }catch (RuntimeException e) {
            sendUnauthorized(response, "Token 无效或已过期");
            return false;
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + msg + "\"}");
    }

}
