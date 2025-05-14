package com.l1Akr.pojo.dao.mapper;

import com.l1Akr.pojo.po.UserRolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserRoleMapper {
    UserRolePO findByUserId(@Param("userId") Integer userId);
    List<UserRolePO> findByRoleId(@Param("roleId") Integer roleId);
    int insert(UserRolePO userRolePO);
    int update(UserRolePO userRolePO);
    int delete(@Param("id") Integer id);
    int deleteByUserIdAndRoleId(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}