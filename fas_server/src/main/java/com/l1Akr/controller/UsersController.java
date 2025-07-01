package com.l1Akr.controller;

import com.github.pagehelper.PageInfo;
import com.l1Akr.annotation.RequiredPermission;
import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.util.JwtUtils;
import com.l1Akr.common.util.UserThreadLocal;
import com.l1Akr.pojo.dto.UserAddDTO;
import com.l1Akr.pojo.dto.UserLineHistoryDTO;
import com.l1Akr.pojo.dto.UserLoginDTO;
import com.l1Akr.pojo.dto.UserRegisterDTO;
import com.l1Akr.pojo.dto.UserUpdateDTO;
import com.l1Akr.pojo.vo.PermissionJwtVO;
import com.l1Akr.pojo.vo.RoleJwtVO;
import com.l1Akr.service.UserService;
import com.l1Akr.pojo.vo.UserInfoVO;
import com.l1Akr.pojo.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import com.l1Akr.common.result.Result;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    private String generateToken(UserInfoVO userInfoVO) {
        Map<String, Object> map = new HashMap<>();
        map.put("roles", userInfoVO.getRoles().stream().map(role -> {
            RoleJwtVO roleJwtVO = new RoleJwtVO();
            BeanUtils.copyProperties(role, roleJwtVO);
            return roleJwtVO;
        }).toList());
        map.put("permissions", userInfoVO.getPermissions().stream().map(permission -> {
            PermissionJwtVO permissionJwtVO = new PermissionJwtVO();
            BeanUtils.copyProperties(permission, permissionJwtVO);
            return permissionJwtVO;
        }).toList());
        return jwtUtils.generateToken(userInfoVO.getId().toString(), map);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("Login User: {}", userLoginDTO);
        UserInfoVO userInfoVO = userService.login(userLoginDTO);
        if(ObjectUtils.isEmpty(userInfoVO)) {
            return new Result<>(USER_PASSWORD_ERROR);
        }

        // 为用户生成token
        String token = generateToken(userInfoVO);

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
    @RequiredPermission(permissions = "user:update", roles = {"ADMIN", "USER"})
    public Result<String> userUpdate(@RequestBody UserUpdateDTO userUpdateDTO) {
        if(ObjectUtils.isEmpty(userUpdateDTO)) {
            throw new BusinessException(Result.ResultEnum.PARAM_ERROR);
        }
        boolean b = userService.updateUser(UserThreadLocal.getCurrentUser().getUserBase().getId().toString(), userUpdateDTO);
        if(!b) {
            throw new BusinessException(Result.ResultEnum.USER_UPDATE_FAILED);
        }
        log.info("用户状态更新成功");
        return Result.success("用户更新成功");
    }

    @Operation(summary = "用户查询")
    @PostMapping("/info")
    @RequiredPermission(permissions = "user:select")
    public Result<String> userInfo() {
        log.info("用户 {} 查询成功", UserThreadLocal.getCurrentUser().getUserBase().getId());
        return Result.success(null);
    }
    
    @Operation(summary = "获取用户总数")
    @GetMapping("/count")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<Integer> getUserCount() {
        int count = userService.getUserCount();
        return Result.success(count);
    }
    
    @Operation(summary = "查询所有用户信息（分页）")
    @GetMapping("/list")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<PageInfo<UserInfoVO>> getAllUsers(
            @RequestParam(defaultValue = "1") @Parameter(name = "pageNum", description = "页码") Integer pageNum,
            @RequestParam(defaultValue = "10") @Parameter(name = "pageSize", description = "页长") Integer pageSize) {
        log.info("pageNum: {}, pageSize: {}", pageNum, pageSize);
        // 如果页码小于1，则返回错误信息
        if(pageNum < 1 || pageSize < 1) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        // 如果页码大于100，则返回错误信息
        if(pageNum > 100 || pageSize > 100) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        PageInfo<UserInfoVO> pageInfo = userService.getAllUsers(pageNum, pageSize);
        return Result.success(pageInfo);
    }
    
    @Operation(summary = "添加用户")
    @PostMapping("/add")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> addUser(@RequestBody UserAddDTO userAddDTO) {
        userService.addUser(userAddDTO);
        return Result.success("用户添加成功");
    }
    
    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{id}")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success("用户删除成功");
    }
    
    @Operation(summary = "更新用户信息")
    @PutMapping("/update/{id}")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO, @PathVariable Integer id) {
        userService.updateUser(id.toString(), userUpdateDTO);
        return Result.success("用户信息更新成功");
    }
    
    /**
     * 查询用户近期创建和更新历史数据
     */
    @Operation(summary = "查询用户近期N天内创建和更新历史数据")
    @GetMapping("/lineHistory")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<List<UserLineHistoryDTO>> getLineHistory(
            @RequestParam(defaultValue = "30") @Parameter(name = "days", description = "天数") Integer days
    ) {
        log.info("查询用户近期{}天内创建和更新历史数据", days);
        List<Integer> daysList = Arrays.asList(7, 30, 90);
        if(!daysList.contains(days)) {
            return new Result<>(Result.ResultEnum.PARAM_ERROR);
        }
        return Result.success(userService.getLineUserHistory(days));
    }

}
