package com.l1Akr.service.implement;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.result.Result;
import com.l1Akr.pojo.dao.mapper.PermissionMapper;
import com.l1Akr.pojo.dto.PermissionAddDTO;
import com.l1Akr.pojo.dto.PermissionUpdateDTO;
import com.l1Akr.pojo.po.PermissionPO;
import com.l1Akr.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public PageInfo<PermissionPO> getAllPermissions(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            Page<PermissionPO> permissionPage = permissionMapper.findAllPermissions();
            return new PageInfo<>(permissionPage);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public void addPermission(PermissionAddDTO permissionAddDTO) {
        PermissionPO permissionPO = new PermissionPO();
        BeanUtils.copyProperties(permissionAddDTO, permissionPO);
        permissionPO.setCreateTime(LocalDateTime.now());
        permissionPO.setUpdateTime(LocalDateTime.now());
        
        PermissionPO tempPermission = new PermissionPO();
        tempPermission.setPermissionName(permissionPO.getPermissionName());
        if(!ObjectUtils.isEmpty(permissionMapper.findByPermissionPO(tempPermission))) {
            throw new BusinessException(Result.ResultEnum.PERMISSION_EXIST);
        }
        
        if(permissionMapper.insert(permissionPO) <= 0) {
            throw new BusinessException(Result.ResultEnum.PERMISSION_ADD_FAILED);
        }
    }

    @Override
    public void deletePermission(Integer id) {
        // 先检查权限是否存在且可删除
        PermissionPO permission = permissionMapper.findById(id);
        if (permission == null) {
            throw new BusinessException(Result.ResultEnum.PERMISSION_NOT_EXIST);
        }
        
        if (!permission.getDeletable()) {
            throw new BusinessException(Result.ResultEnum.PERMISSION_NOT_DELETABLE);
        }
        
        if(permissionMapper.delete(id) <= 0) {
            throw new BusinessException(Result.ResultEnum.PERMISSION_DELETE_FAILED);
        }
    }

    @Override
    public void updatePermission(PermissionUpdateDTO permissionUpdateDTO) {
        // 先检查权限是否存在且可修改
        PermissionPO existingPermission = permissionMapper.findById(permissionUpdateDTO.getId());
        if (existingPermission == null) {
            throw new BusinessException(Result.ResultEnum.PERMISSION_NOT_EXIST);
        }
        
        if (!existingPermission.getDeletable()) {
            throw new BusinessException(Result.ResultEnum.PERMISSION_NOT_UPDATABLE);
        }
        
        PermissionPO permissionPO = new PermissionPO();
        BeanUtils.copyProperties(permissionUpdateDTO, permissionPO);
        permissionPO.setUpdateTime(LocalDateTime.now());
        
        if(permissionMapper.update(permissionPO) <= 0) {
            throw new BusinessException(Result.ResultEnum.PERMISSION_NOT_EXIST);
        }
    }
} 