package com.l1Akr.service.implement;

import com.l1Akr.common.utils.ShaUtils;
import com.l1Akr.dto.UserDTO;
import com.l1Akr.dao.UserDAO;
import com.l1Akr.mapper.UserMapper;
import com.l1Akr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    public final UserMapper userMapper;

    /**
     * 用户登录
     * @param userDTO 用户传递对象
     * @return Boolean
     */
    @Override
    public Boolean login(UserDTO userDTO){
        return true;
    }

    /**
     * 用户注册
     * @param userDTO 用户传递对象
     * @return Boolean
     */
    @Override
    public UserDAO register(UserDTO userDTO){
        UserDAO userDAO = new UserDAO();
        BeanUtils.copyProperties(userDTO, userDAO);
        ShaUtils shaUtils = new ShaUtils();
        userDAO.setPassword(shaUtils.SHA256(userDAO.getPassword()));
        userDAO.setCreateTime(new Date());
        userDAO.setUpdateTime(new Date());
        // 由于前端暂时没有设置telephone，这里直接手动设置
        userDAO.setTelephone("12345678901");

        UserDAO userDAO1 = userMapper.InsertByUser(userDAO);
        if(userDAO1 == null){
            return null;
        }

        return userDAO1;
    }
}
