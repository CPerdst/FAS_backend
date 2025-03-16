package com.l1Akr.service.implement;

import com.l1Akr.common.enums.ResultEnum;
import com.l1Akr.common.exception.BusinessException;
import com.l1Akr.common.utils.ShaUtils;
import com.l1Akr.dto.UserLoginDTO;
import com.l1Akr.dto.UserRegisterDTO;
import com.l1Akr.dao.UserDAO;
import com.l1Akr.mapper.UserMapper;
import com.l1Akr.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param UserLoginDTO 用户传递对象
     * @return Boolean
     */
    @Override
    public UserDAO login(UserLoginDTO userLoginDTO){
        ShaUtils shaUtils = new ShaUtils();
        UserDAO userDAO = new UserDAO();
        BeanUtils.copyProperties(userLoginDTO,userDAO);
        userDAO.setPassword(shaUtils.SHA256(userLoginDTO.getPassword()));
        userDAO.setSex(null);
        UserDAO byUser = userMapper.findByUser(userDAO);
        if(byUser.id == null){
            throw new BusinessException(ResultEnum.USER_LOGIN_ERROR);
        }
        return byUser;
    }

    /**
     * 用户注册
     * @param userRegisterDTO 用户传递对象
     * @return Boolean
     */
    @Override
    public UserDAO register(UserRegisterDTO userRegisterDTO){
        UserDAO userDAO = new UserDAO();
        BeanUtils.copyProperties(userRegisterDTO, userDAO);
        ShaUtils shaUtils = new ShaUtils();
        userDAO.setPassword(shaUtils.SHA256(userDAO.getPassword()));
        userDAO.setCreateTime(new Date());
        userDAO.setUpdateTime(new Date());
        // 由于前端暂时没有设置telephone，这里直接手动设置
        userDAO.setTelephone("12345678901");

        try {
            userMapper.insertByUser(userDAO);
        }catch (Exception e){
            throw new BusinessException(ResultEnum.USER_HAS_EXISTED);
        }

        return userDAO;
    }
}
