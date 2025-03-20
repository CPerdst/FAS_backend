package com.l1Akr.service;

import com.l1Akr.dao.UserDAO;
import com.l1Akr.dto.UserLoginDTO;
import com.l1Akr.dto.UserRegisterDTO;
import org.springframework.stereotype.Component;

@Component
public interface UserService {

    /**
     * 用户登录
     * @param UserLoginDTO
     * @return
     */
    public UserDAO login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    public UserDAO register(UserRegisterDTO userRegisterDTO);

    /**
     * 通过用户id获取用户
     * @param userId
     * @return
     */
    public UserDAO getUserById(String userId);
}
