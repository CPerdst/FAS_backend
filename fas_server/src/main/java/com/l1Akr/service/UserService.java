package com.l1Akr.service;

import com.l1Akr.dto.UserLoginDTO;
import com.l1Akr.dto.UserRegisterDTO;
import com.l1Akr.dto.UserUpdateDTO;
import com.l1Akr.po.UserBasePO;
import com.l1Akr.vo.UserInfoVO;
import org.springframework.stereotype.Component;

@Component
public interface UserService {

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    UserInfoVO login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    void register(UserRegisterDTO userRegisterDTO);

    /**
     * 用户更新
     * @param userUpdateDTO
     * @return
     */
    boolean updateUser(String userId, UserUpdateDTO userUpdateDTO);

    /**
     * 通过UserBasePO更新用户
     * @param userBasePO
     * @return
     */
    int updateUserByUserBasePo(UserBasePO userBasePO);

    /**
     * 通过用户id获取用户
     * @param userId
     * @return
     */
    UserBasePO getUserById(String userId);

    /**
     * 通过用户Id获取用户头像地址
     * @param userId
     * @return
     */
    String getAvatarById(String userId);
}
