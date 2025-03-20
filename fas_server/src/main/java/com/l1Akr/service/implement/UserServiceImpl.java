package com.l1Akr.service.implement;

import com.l1Akr.common.enums.ResultEnum;
import com.l1Akr.common.exceptionss.BusinessException;
import com.l1Akr.common.exceptionss.UserHasExistedException;
import com.l1Akr.common.exceptionss.UserNotFoundException;
import com.l1Akr.common.exceptionss.UserOrPasswordErrorException;
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
        // 哈希工具
        ShaUtils shaUtils = new ShaUtils();

        // 创建用户的数据库映射模型
        UserDAO userDAO = new UserDAO();
        BeanUtils.copyProperties(userLoginDTO, userDAO);
        userDAO.setPassword(null);

        // 单独通过username查询用户
        UserDAO byUser = userMapper.findByUser(userDAO);

        // 如果查询为空，表示用户不存在
//        if(byUser == null){
//            throw new BusinessException(ResultEnum.USER_NOT_EXIST);
//        }

        // 验证密码，不正确则抛出用户登陆错误异常
//        if(!byUser.password.equals(shaUtils.SHA256(userLoginDTO.getPassword()))){
//            throw new BusinessException(ResultEnum.USER_LOGIN_ERROR);
//        }

        // 将两个逻辑合在一起并返回用户或密码错误
        if(byUser == null || !byUser.password.equals(shaUtils.SHA256(userLoginDTO.getPassword()))){
            throw new UserOrPasswordErrorException();
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
        // 哈希工具
        ShaUtils shaUtils = new ShaUtils();

        // 创建用户的数据库映射模型
        UserDAO userDAO = new UserDAO();
        BeanUtils.copyProperties(userRegisterDTO, userDAO);

        userDAO.setPassword(shaUtils.SHA256(userDAO.getPassword()));
        userDAO.setCreateTime(new Date());
        userDAO.setUpdateTime(new Date());
        // 由于前端暂时没有设置telephone，这里直接手动设置
        userDAO.setTelephone("12345678901");

        try {
            userMapper.insertByUser(userDAO);
        }catch (Exception e){
            throw new UserHasExistedException();
        }

        return userDAO;
    }

    /**
     * 通过用户id获取用户
     * @param userId
     * @return
     */
    public UserDAO getUserById(String userId) {
        UserDAO userDAO = new UserDAO();
        userDAO.setId(Integer.valueOf(userId));

        return userMapper.findByUser(userDAO);
    }

    /**
     * 更新用户信息
     * @param userDAO
     */
    public void updateUser(UserDAO userDAO) {
        if(userDAO.getId() == null){
            return;
        }
        userMapper.updateByUser(userDAO);
    }



}
