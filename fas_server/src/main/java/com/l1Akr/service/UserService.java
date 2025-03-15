package com.l1Akr.service;

import com.l1Akr.dao.UserDAO;
import com.l1Akr.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public interface UserService {

    /**
     * 用户登录
     * @param userDTO
     * @return
     */
    public Boolean login(UserDTO userDTO);

    /**
     * 用户注册
     * @param userDTO
     * @return
     */
    public UserDAO register(UserDTO userDTO);
}
