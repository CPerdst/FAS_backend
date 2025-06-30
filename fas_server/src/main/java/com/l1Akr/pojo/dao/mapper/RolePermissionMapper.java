package com.l1Akr.pojo.dao.mapper;

import com.l1Akr.pojo.po.RolePermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RolePermissionMapper {
    RolePermissionPO findById(@Param("id") Integer id);
    List<RolePermissionPO> findByRoleId(@Param("roleId") Integer roleId);
    List<RolePermissionPO> findByPermissionId(@Param("permissionId") Integer permissionId);
    int insert(RolePermissionPO rolePermissionPO);
    int update(RolePermissionPO rolePermissionPO);
    int delete(@Param("id") Integer id);
}