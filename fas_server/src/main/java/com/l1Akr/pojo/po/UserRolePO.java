package com.l1Akr.pojo.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserRolePO {
    public Integer id;
    public Integer userId;
    public Integer roleId;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}