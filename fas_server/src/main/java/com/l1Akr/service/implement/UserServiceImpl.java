package com.l1Akr.service.implement;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.util.ShaUtils;
import com.l1Akr.pojo.dto.UserAddDTO;
import com.l1Akr.pojo.dto.UserLoginDTO;
import com.l1Akr.pojo.dto.UserRegisterDTO;
import com.l1Akr.pojo.dto.UserUpdateDTO;
import com.l1Akr.pojo.dao.mapper.PermissionMapper;
import com.l1Akr.pojo.dao.mapper.RoleMapper;
import com.l1Akr.pojo.dao.mapper.UserMapper;
import com.l1Akr.pojo.dao.mapper.UserRoleMapper;
import com.l1Akr.pojo.po.PermissionPO;
import com.l1Akr.pojo.po.RolePO;
import com.l1Akr.pojo.po.UserBasePO;
import com.l1Akr.pojo.po.UserRolePO;
import com.l1Akr.service.UserService;
import com.l1Akr.pojo.vo.UserInfoVO;
import io.micrometer.common.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;

    private final ShaUtils shaUtils = new ShaUtils();

    private final Integer USER_ROLE_ID = 2;

    /**
     * 用户登录
     * @param userLoginDTO 用户登录传递对象
     * @return Boolean
     */
    @Override
    public UserInfoVO login(UserLoginDTO userLoginDTO) {
//        String userId = UserThreadLocal.getCurrentUser().getId().toString();
//        // 获取用户角色信息
//        List<RolePO> roles = getRolesByUserId(userId);
//        // 获取用户权限信息
//        List<PermissionPO> permissions = getPermissionsByUserId(userId);
        // 创建用户的数据库映射模型
        UserBasePO userBasePO = new UserBasePO();
        BeanUtils.copyProperties(userLoginDTO, userBasePO);
        userBasePO.setPassword(shaUtils.SHA256(userLoginDTO.getPassword()));

        // 单独通过username查询用户
        UserBasePO byUser = userMapper.findByUserBasePo(userBasePO);
        if(ObjectUtils.isEmpty(byUser)) {
            return null;
        }
        String userId = byUser.getId().toString();
        List<RolePO> roles = getRolesByUserId(userId);
        List<PermissionPO> permissions = getPermissionsByUserId(userId);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(byUser, userInfoVO);
        userInfoVO.setRoles(roles);
        userInfoVO.setPermissions(permissions);
        return userInfoVO;
    }

    /**
     * 用户注册
     * @param userRegisterDTO 用户传递对象
     * @return Boolean
     */
    @Override
    public void register(UserRegisterDTO userRegisterDTO){
        // 哈希工具
        ShaUtils shaUtils = new ShaUtils();

        // 创建用户的数据库映射模型
        UserBasePO userBasePO = new UserBasePO();
        BeanUtils.copyProperties(userRegisterDTO, userBasePO);

        userBasePO.setPassword(shaUtils.SHA256(userBasePO.getPassword()));
        userBasePO.initTime();
        // 由于前端暂时没有设置telephone，这里直接手动设置
        userBasePO.setTelephone("12345678901");

        // 首先检测一下注册的用户是否存在
        UserBasePO tempUser = new UserBasePO();
        tempUser.setUsername(userBasePO.getUsername());
        UserBasePO byUserBasePo = userMapper.findByUserBasePo(tempUser);
        if(!ObjectUtils.isEmpty(byUserBasePo)) {
            throw new BusinessException(Result.ResultEnum.USER_EXIST);
        }

        // 如果不存在，再继续插入
        userMapper.insertByUserBasePo(userBasePO);
        // 同时为该用户添加默认USER角色
        assignRoleToUser(userBasePO.getId().toString(), USER_ROLE_ID);
    }

    /**
     * 用户更新
     * @param userUpdateDTO
     * @return
     */
    @Override
    public boolean updateUser(String userId, UserUpdateDTO userUpdateDTO) {
        String username = userUpdateDTO.getUsername();
        String oldPassword = userUpdateDTO.getOldPassword();
        String newPassword = userUpdateDTO.getNewPassword();

        if(StringUtils.isBlank(username)) {
            throw new BusinessException(Result.ResultEnum.USER_NAME_EMPTY);
        }
        if(!StringUtils.isBlank(oldPassword) && !StringUtils.isBlank(newPassword) && oldPassword.equals(newPassword)) {
            throw new BusinessException(Result.ResultEnum.USER_PASSWORD_SAME);
        }
        // 首先通过用户ID查找该用户
        UserBasePO userById = getUserById(userId);
        if(ObjectUtils.isEmpty(userById)) {
            throw new BusinessException(Result.ResultEnum.USER_NOT_EXIST);
        }

        UserBasePO userBasePO = new UserBasePO();
        userBasePO.initUpdateDate();
        userBasePO.setUsername(username);
        userBasePO.setSex(userUpdateDTO.getSex());
        userBasePO.setId(Integer.valueOf(userId));
        // 判断是否要更新密码，并且判断旧密码是否正确
        if(!StringUtils.isBlank(oldPassword) && !StringUtils.isBlank(newPassword)) {
            if(userById.getPassword().equals(shaUtils.SHA256(oldPassword))) {
                // 如果密码验证成功，才可以更改密码
                userBasePO.setPassword(shaUtils.SHA256(newPassword));
            } else {
                // 如果密码验证失败，返回认证失败的信息
                throw new BusinessException(Result.ResultEnum.USER_PASSWORD_ERROR);
            }
        }

        // 进行更新
        int affectedRows = updateUserByUserBasePo(userBasePO);
        if(affectedRows <= 0) {
            throw new BusinessException(Result.ResultEnum.USER_UPDATE_FAILED);
        }

        return true;
    }

    /**
     * 更新用户
     * @param userBasePO
     * @return
     */
    @Override
    public int updateUserByUserBasePo(UserBasePO userBasePO) {
        return userMapper.updateByUserBasePo(userBasePO);
    }

    /**
     * 通过用户id获取用户
     * @param userId
     * @return
     */
    public UserBasePO getUserById(String userId) {
        UserBasePO userBasePO = new UserBasePO();
        userBasePO.setId(Integer.valueOf(userId));

        return userMapper.findByUserBasePo(userBasePO);
    }
    
    public List<RolePO> getRolesByUserId(String userId) {
        return roleMapper.findByUserId(Integer.valueOf(userId));
    }
    
    public List<PermissionPO> getPermissionsByUserId(String userId) {
        return permissionMapper.findByUserId(Integer.valueOf(userId));
    }
    
    public boolean hasPermission(String userId, String permission) {
        List<PermissionPO> permissions = getPermissionsByUserId(userId);
        return permissions.stream().anyMatch(p -> p.getPermissionName().equals(permission));
    }
    
    public boolean hasRole(String userId, String roleName) {
        List<RolePO> roles = getRolesByUserId(userId);
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }
    
    public void assignRoleToUser(String userId, Integer roleId) {
        UserRolePO userRolePO = new UserRolePO();
        userRolePO.setUserId(Integer.valueOf(userId));
        userRolePO.setRoleId(roleId);
        userRolePO.setCreateTime(LocalDateTime.now());
        userRolePO.setUpdateTime(LocalDateTime.now());
        userRoleMapper.insert(userRolePO);
    }
    
    public void removeRoleFromUser(String userId, Integer roleId) {
        userRoleMapper.deleteByUserIdAndRoleId(Integer.valueOf(userId), roleId);
    }

    /**
     * 通过用户Id获取用户头像地址
     * @param userId
     * @return
     */
    public String getAvatarById(String userId) {
        UserBasePO userBasePO = new UserBasePO();
        userBasePO.setId(Integer.valueOf(userId));
        return userMapper.findByUserBasePo(userBasePO).getAvatar();
    }
    
    @Override
    public int getUserCount() {
        return userMapper.getUserCount();
    }
    
    @Override
    public PageInfo<UserInfoVO> getAllUsers(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            Page<UserBasePO> userPage = userMapper.findAllUsers();
            List<UserInfoVO> userInfoVOList = userPage.stream().map(user -> {
                UserInfoVO userInfoVO = new UserInfoVO();
                BeanUtils.copyProperties(user, userInfoVO);
                String userId = user.getId().toString();
                userInfoVO.setRoles(getRolesByUserId(userId));
                userInfoVO.setPermissions(getPermissionsByUserId(userId));
                return userInfoVO;
            }).collect(Collectors.toList());
            
            PageInfo<UserInfoVO> pageInfo = new PageInfo<>(userInfoVOList);
            pageInfo.setTotal(userPage.getTotal());
            pageInfo.setPageNum(userPage.getPageNum());
            pageInfo.setPageSize(userPage.getPageSize());
            pageInfo.setPages(userPage.getPages());
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }
    
    @Override
    public void addUser(UserAddDTO userAddDTO) {
        UserBasePO userBasePO = new UserBasePO();
        BeanUtils.copyProperties(userAddDTO, userBasePO);
        userBasePO.setPassword(shaUtils.SHA256(userAddDTO.getPassword()));
        userBasePO.initTime();
        
        UserBasePO tempUser = new UserBasePO();
        tempUser.setUsername(userBasePO.getUsername());
        if(!ObjectUtils.isEmpty(userMapper.findByUserBasePo(tempUser))) {
            throw new BusinessException(Result.ResultEnum.USER_EXIST);
        }
        
        userMapper.insertByUserBasePo(userBasePO);
        // 为用户添加默认USER角色
        assignRoleToUser(userBasePO.getId().toString(), USER_ROLE_ID);
    }
    
    @Override
    public void deleteUser(Integer id) {
        if(userMapper.deleteByUserId(id) <= 0) {
            throw new BusinessException(Result.ResultEnum.USER_NOT_EXIST);
        }
    }
    
    @Override
    public void updateUserInfo(UserUpdateDTO userUpdateDTO, Integer id) {
        UserBasePO userBasePO = new UserBasePO();
        BeanUtils.copyProperties(userUpdateDTO, userBasePO);
        userBasePO.initUpdateDate();
        
        if(userMapper.updateByUserBasePo(userBasePO) <= 0) {
            throw new BusinessException(Result.ResultEnum.USER_UPDATE_FAILED);
        }
    }

}
