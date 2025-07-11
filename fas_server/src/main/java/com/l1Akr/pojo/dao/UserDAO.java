package com.l1Akr.pojo.dao;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO {
    public Integer id;
    public String username;
    public String password;
    public Boolean sex;
    public String telephone;
    public Date createTime;
    public Date updateTime;

    public void initTime() {
        this.createTime = new Date();
        this.updateTime = new Date();
    }
}
