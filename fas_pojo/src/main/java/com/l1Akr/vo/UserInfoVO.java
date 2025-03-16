package com.l1Akr.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {
    public Integer id;
    public String username;
    public Boolean sex;
    public String telephone;
    public Date createTime;
    public Date updateTime;
}
