package com.l1Akr.pojo.dto;

import java.util.List;

import com.l1Akr.pojo.po.PermissionPO;
import com.l1Akr.pojo.po.RolePO;
import com.l1Akr.pojo.po.UserBasePO;

import com.l1Akr.pojo.vo.PermissionJwtVO;
import com.l1Akr.pojo.vo.RoleJwtVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InnerUserInfo {
    private UserBasePO userBase;
    private List<RoleJwtVO> roles;
    private List<PermissionJwtVO> permissions;
}