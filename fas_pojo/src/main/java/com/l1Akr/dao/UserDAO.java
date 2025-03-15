package com.l1Akr.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO {
    private Integer id;
    private String username;
    private String password;
    private Boolean sex;
    private String telephone;
    private Date createTime;
    private Date updateTime;
}
