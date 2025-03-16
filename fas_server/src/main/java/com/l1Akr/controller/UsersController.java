package com.l1Akr.controller;

import com.l1Akr.common.utils.JwtUtils;
import com.l1Akr.dao.UserDAO;
import com.l1Akr.dto.UserLoginDTO;
import com.l1Akr.dto.UserRegisterDTO;
import com.l1Akr.service.UserService;
import com.l1Akr.vo.UserInfoVO;
import com.l1Akr.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
@RequestMapping("/user")
@Tag(name = "用户管理模块", description = "用户的CURD操作")
public class UsersController {

    @Autowired
    public UserService userService;

    @Autowired
    public JwtUtils jwtUtils;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("Login User: {}", userLoginDTO);
        UserDAO user = userService.login(userLoginDTO);

        Map<String, Object> map = new HashMap<>();

        String token = jwtUtils.generateToken(user.getId().toString(), map);

        UserVO userVO = new UserVO();
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        userVO.setToken(token);
        userVO.setUser(userInfoVO);

        return Result.success(userVO);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("Register User: {}", userRegisterDTO);
        userService.register(userRegisterDTO);
        return Result.success("注册成功");
    }


}
