package com.l1Akr.controller;

import com.l1Akr.common.utils.JwtUtils;
import com.l1Akr.dao.UserDAO;
import com.l1Akr.dto.UserDTO;
import com.l1Akr.service.UserService;
import com.l1Akr.service.implement.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.l1Akr.common.result.Result;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/user")
@Tag(name = "用户管理模块", description = "用户的CURD操作")
public class UsersController {

    @Autowired
    public UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        JwtUtils jwtUtils = new JwtUtils();
        log.info("Login User: {}", userDTO);
        Map<String, Object> map = new HashMap<>();
        map.put("username", userDTO.getUsername());
        map.put("password", userDTO.getPassword());
        String token = jwtUtils.generateToken(userDTO.getUsername(), map);
        return Result.success(token);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody UserDTO userDTO) {
        log.info("Register User: {}", userDTO);
        UserDAO register = userService.register(userDTO);
        return Result.success(true);
    }


}
