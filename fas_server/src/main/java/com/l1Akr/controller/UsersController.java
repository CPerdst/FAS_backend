package com.l1Akr.controller;

import com.l1Akr.common.enums.ResultEnum;
import com.l1Akr.common.exceptionss.*;
import com.l1Akr.common.utils.JwtUtils;
import com.l1Akr.common.utils.OssUtils;
import com.l1Akr.common.utils.ShaUtils;
import com.l1Akr.common.utils.UserThreadLocal;
import com.l1Akr.dao.UserDAO;
import com.l1Akr.dto.UserLoginDTO;
import com.l1Akr.dto.UserRegisterDTO;
import com.l1Akr.dto.UserUpdateDTO;
import com.l1Akr.service.UserService;
import com.l1Akr.vo.UserInfoVO;
import com.l1Akr.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.l1Akr.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/user")
@Tag(name = "用户管理模块", description = "用户的CURD操作")
public class UsersController {

    @Autowired
    public UserService userService;

    @Autowired
    public JwtUtils jwtUtils;

    @Autowired
    public OssUtils ossUtils;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO userLoginDTO) {
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

//    @Operation(summary = "用户上传头像")
//    @PostMapping("/avatar/upload")
//    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
//        String s;
//        try {
//            // 校验文件
//            if(!isValidFile(file)) {
//                return Result.error("仅支持JPG/PNG格式且不超过2MB");
//            }
//
//            // 生成唯一文件名
//            UserDAO user = UserThreadLocal.getCurrentUser();
//            String userId = user.getId().toString();
//            String fileName = ossUtils.generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()), userId);
//
//            // 上传到Oss
//            s = ossUtils.uploadAvatar(file, fileName);
//            if (s == null) {
//                return Result.error("上传失败，请重试");
//            }
//
//            // 更新数据库
//            UserDAO newUser = new UserDAO();
//            newUser.setId(user.getId());
//            newUser.setAvatar(s);
//            newUser.setUpdateTime(new Date());
//            userService.updateUser(newUser);
//        } catch (IOException e) {
//            log.error("文件上传失败", e); // 记录日志
//            throw new FileUploadFailedException();
//        } catch (MyBatisSystemException e) {
//            log.error("MyBatis异常", e);
//            throw new MyBatisException();
//        }
//
//        return Result.success(s);
//    }

//    @Operation(summary = "根据用户id获取用户头像地址")
//    @GetMapping("/avatar")
//    public Result<String> avatarGet() {
//        return Result.success(userService.getAvatarById(UserThreadLocal.getCurrentUser().getId().toString()));
//    }

    @Operation(summary = "用户更新")
    @PostMapping("/update")
    public Result<String> userUpdate(@RequestBody UserUpdateDTO userUpdateDTO) {
        UserDAO userDAO;
        ShaUtils shaUtils = new ShaUtils();
        String oldPassword = userUpdateDTO.getOldPassword();
        String newPassword = userUpdateDTO.getNewPassword();

        // 在密码不为空的情况下，先判断用户的新旧密码是否一样
        if(!oldPassword.isEmpty() && !newPassword.isEmpty() && oldPassword.equals(newPassword)) {
            throw new DataPasswordErrorException();
        }

        // 首先根据用户Id查询用户DAO
        userDAO = userService.getUserById(UserThreadLocal.getCurrentUser().getId().toString());
        if(userDAO == null) {
            throw new UserNotExistException();
        }

        // 如果需要更改密码，就去验证密码
        if(!oldPassword.isEmpty() && !newPassword.isEmpty()) {
            // 判断密码
            if(!shaUtils.SHA256(userUpdateDTO.getOldPassword()).equals(userDAO.getPassword())) {
                throw new UserOrPasswordErrorException();
            }
        }

        if((oldPassword.isEmpty() && !newPassword.isEmpty()) ||
        (!oldPassword.isEmpty() && newPassword.isEmpty())) {
            throw new BusinessException(ResultEnum.FAIL);
        }

        UserDAO newUser = new UserDAO();
        newUser.setId(userDAO.getId());
        BeanUtils.copyProperties(userUpdateDTO, newUser);
        newUser.setPassword(
                newPassword.isEmpty() ? null : shaUtils.SHA256(newPassword)
        );
        newUser.setUpdateTime(new Date());
        userService.updateUser(newUser);

//        // 如果有密码
//        if(!oldPassword.isEmpty() && !newPassword.isEmpty()) {
//            // 判断密码
//            if(!shaUtils.SHA256(userUpdateDTO.getOldPassword()).equals(userDAO.getPassword())) {
//                throw new UserOrPasswordErrorException();
//            }
//            // 认证成功，更新用户数据
//            userDAO.setPassword(shaUtils.SHA256(userUpdateDTO.getNewPassword()));
//            userDAO.setUpdateTime(new Date());
//            userService.updateUser(userDAO);
//        }else { // 如果没有密码
//            // 判断用户名和性别是否改过
//            // 没改过，更新时间，并直接返回成功
//            if(userUpdateDTO.getSex().equals(userDAO.getSex()) &&
//            userUpdateDTO.getUsername().equals(userDAO.getUsername())) {
//                userDAO.setUpdateTime(new Date());
//                userService.updateUser(userDAO);
//            }else {
//                // 改过，则更新改动的内容，然后返回成功响应
//                userDAO.setUpdateTime(new Date());
//                userDAO.setPassword(shaUtils.SHA256(newPassword));
//                userDAO.setUsername(userUpdateDTO.getUsername());
//                userDAO.setSex(userUpdateDTO.getSex());
//                userService.updateUser(userDAO);
//            }
//        }
        return Result.success("用户更新成功");
    }

    // 文件校验方法
//    private boolean isValidFile(MultipartFile file) {
//        String contentType = file.getContentType();
//        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
//            return false;
//        }
//        return file.getSize() <= 2 * 1024 * 1024; // 2MB限制
//    }


}
