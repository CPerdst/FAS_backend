package com.l1Akr.service;

import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dto.RoleAddDTO;
import com.l1Akr.pojo.dto.RoleUpdateDTO;
import com.l1Akr.pojo.po.RolePO;

public interface RoleService {
    
    PageInfo<RolePO> getAllRoles(int pageNum, int pageSize);
    
    void addRole(RoleAddDTO roleAddDTO);
    
    void deleteRole(Integer id);
    
    void updateRole(RoleUpdateDTO roleUpdateDTO);
}