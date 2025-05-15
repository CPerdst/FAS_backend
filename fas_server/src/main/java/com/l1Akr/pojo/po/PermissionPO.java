package com.l1Akr.pojo.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PermissionPO {
    public Integer id;
    public String permissionName;
    public String permissionDesc;
    public Boolean deletable;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}