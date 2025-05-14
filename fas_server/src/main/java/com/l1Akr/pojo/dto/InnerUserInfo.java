package com.l1Akr.pojo.dto;

import java.util.List;

import com.l1Akr.pojo.po.PermissionPO;
import com.l1Akr.pojo.po.RolePO;
import com.l1Akr.pojo.po.UserBasePO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InnerUserInfo {
    private UserBasePO userBase;
    private List<RolePO> roles;
    private List<PermissionPO> permissions;
}