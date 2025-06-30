package com.l1Akr.service;

import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dto.PermissionAddDTO;
import com.l1Akr.pojo.dto.PermissionUpdateDTO;
import com.l1Akr.pojo.po.PermissionPO;

public interface PermissionService {
    
    PageInfo<PermissionPO> getAllPermissions(int pageNum, int pageSize);
    
    void addPermission(PermissionAddDTO permissionAddDTO);
    
    void deletePermission(Integer id);
    
    void updatePermission(PermissionUpdateDTO permissionUpdateDTO);
} 