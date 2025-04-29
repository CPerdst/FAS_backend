package com.l1Akr.controller;

import com.l1Akr.common.exceptionss.*;
import com.l1Akr.common.utils.JwtUtils;
import com.l1Akr.common.utils.UserThreadLocal;
import com.l1Akr.dto.UserLoginDTO;
import com.l1Akr.dto.UserRegisterDTO;
import com.l1Akr.dto.UserUpdateDTO;
import com.l1Akr.service.UserService;
import com.l1Akr.vo.UserInfoVO;
import com.l1Akr.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import com.l1Akr.common.result.Result;
import java.util.HashMap;
import java.util.Map;

import static com.l1Akr.common.result.Result.ResultEnum.USER_PASSWORD_ERROR;

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
    public Result<UserVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("Login User: {}", userLoginDTO);
        UserInfoVO userInfoVO = userService.login(userLoginDTO);
        if(ObjectUtils.isEmpty(userInfoVO)) {
            return new Result<>(USER_PASSWORD_ERROR);
        }

        // 为用户生成token
        Map<String, Object> map = new HashMap<>();
        String token = jwtUtils.generateToken(userInfoVO.getId().toString(), map);

        UserVO userVO = new UserVO();
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

    @Operation(summary = "用户更新")
    @PostMapping("/update")
    public Result<String> userUpdate(@RequestBody UserUpdateDTO userUpdateDTO) {
        if(ObjectUtils.isEmpty(userUpdateDTO)) {
            throw new BusinessException(Result.ResultEnum.PARAM_ERROR);
        }
        boolean b = userService.updateUser(UserThreadLocal.getCurrentUser().getId().toString(), userUpdateDTO);
        if(!b) {
            throw new BusinessException(Result.ResultEnum.USER_UPDATE_FAILED);
        }
        log.info("用户状态更新成功");
        return Result.success("用户更新成功");
    }

}
