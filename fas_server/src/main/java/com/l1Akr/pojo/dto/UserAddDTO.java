package com.l1Akr.pojo.dto;

import lombok.Data;

@Data
public class UserAddDTO {
    private String username;
    private String password;
    private Boolean sex;
    private String telephone;
}