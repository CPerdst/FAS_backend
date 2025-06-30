package com.l1Akr.service.implement;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.result.Result;
import com.l1Akr.pojo.dao.mapper.RoleMapper;
import com.l1Akr.pojo.dto.RoleAddDTO;
import com.l1Akr.pojo.dto.RoleUpdateDTO;
import com.l1Akr.pojo.po.RolePO;
import com.l1Akr.service.RoleService;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public PageInfo<RolePO> getAllRoles(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            Page<RolePO> rolePage = roleMapper.findAllRoles();
            return new PageInfo<>(rolePage);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public void addRole(RoleAddDTO roleAddDTO) {
        RolePO rolePO = new RolePO();
        BeanUtils.copyProperties(roleAddDTO, rolePO);
        rolePO.setCreateTime(LocalDateTime.now());
        rolePO.setUpdateTime(LocalDateTime.now());
        
        RolePO tempRole = new RolePO();
        tempRole.setName(rolePO.getName());
        if(!ObjectUtils.isEmpty(roleMapper.findByRolePO(tempRole))) {
            throw new BusinessException(Result.ResultEnum.ROLE_EXIST);
        }
        
        roleMapper.insertByRolePO(rolePO);
    }

    @Override
    public void deleteRole(Integer id) {
        // 先检查角色是否存在且可删除
        RolePO role = roleMapper.findById(id);
        if (role == null) {
            throw new BusinessException(Result.ResultEnum.ROLE_NOT_EXIST);
        }
        
        if (!role.getDeletable()) {
            throw new BusinessException(Result.ResultEnum.ROLE_NOT_DELETABLE);
        }
        
        if(roleMapper.deleteById(id) <= 0) {
            throw new BusinessException(Result.ResultEnum.ROLE_DELETE_FAILED);
        }
    }

    @Override
    public void updateRole(RoleUpdateDTO roleUpdateDTO) {
        // 先检查角色是否存在且可修改
        RolePO existingRole = roleMapper.findById(roleUpdateDTO.getId());
        if (existingRole == null) {
            throw new BusinessException(Result.ResultEnum.ROLE_NOT_EXIST);
        }
        
        if (!existingRole.getDeletable()) {
            throw new BusinessException(Result.ResultEnum.ROLE_NOT_UPDATABLE);
        }
        
        RolePO rolePO = new RolePO();
        BeanUtils.copyProperties(roleUpdateDTO, rolePO);
        rolePO.setUpdateTime(LocalDateTime.now());
        
        if(roleMapper.update(rolePO) <= 0) {
            throw new BusinessException(Result.ResultEnum.ROLE_UPDATE_FAILED);
        }
    }
}