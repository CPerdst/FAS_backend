package com.l1Akr.pojo.vo;

import com.l1Akr.pojo.po.PermissionPO;
import com.l1Akr.pojo.po.RolePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {
    public Integer id;
    public String username;
    public String avatar;
    public Boolean sex;
    public String telephone;
    public Date createTime;
    public Date updateTime;
    public List<RolePO> roles;
    public List<PermissionPO> permissions;
}
