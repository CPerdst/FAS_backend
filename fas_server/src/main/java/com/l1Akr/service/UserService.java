package com.l1Akr.service;

import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dto.UserAddDTO;
import com.l1Akr.pojo.dto.UserLineHistoryDTO;
import com.l1Akr.pojo.dto.UserLoginDTO;
import com.l1Akr.pojo.dto.UserRegisterDTO;
import com.l1Akr.pojo.dto.UserUpdateDTO;
import com.l1Akr.pojo.po.UserBasePO;
import com.l1Akr.pojo.vo.UserInfoVO;

import java.util.List;

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
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    int getUserCount();


    /**
     * 检查用户是否有指定的权限
     * @param userId
     * @param permission
     * @return
     */
    boolean hasPermission(String userId, String permission);

    public PageInfo<UserInfoVO> getAllUsers(int pageNum, int pageSize);

    public void addUser(UserAddDTO userAddDTO);

    public void deleteUser(Integer id);

    public void updateUserInfo(UserUpdateDTO userUpdateDTO, Integer id);
    
    /**
     * 查询用户近期创建和更新历史数据
     * @param days 天数
     * @return 用户历史数据列表
     */
    List<UserLineHistoryDTO> getLineUserHistory(int days);
}
