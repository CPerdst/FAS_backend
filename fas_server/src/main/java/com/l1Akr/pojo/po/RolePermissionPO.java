package com.l1Akr.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionPO {
    public Integer id;
    public Integer roleId;
    public Integer permissionId;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}