package com.l1Akr.pojo.dao.mapper;

import com.github.pagehelper.Page;
import com.l1Akr.pojo.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface PermissionMapper {
    PermissionPO findById(@Param("id") Integer id);
    List<PermissionPO> findAll();
    int insert(PermissionPO permissionPO);
    int update(PermissionPO permissionPO);
    int delete(@Param("id") Integer id);
    List<PermissionPO> findByUserId(@Param("userId") Integer userId);
    
    Page<PermissionPO> findAllPermissions();
    
    PermissionPO findByPermissionPO(PermissionPO permissionPO);
}