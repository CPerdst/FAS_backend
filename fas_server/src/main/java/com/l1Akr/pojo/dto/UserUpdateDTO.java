package com.l1Akr.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    String username;
    String oldPassword;
    String newPassword;
    Boolean sex;
}
