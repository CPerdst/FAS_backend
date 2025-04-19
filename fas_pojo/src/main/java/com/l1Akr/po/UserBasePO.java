package com.l1Akr.po;

import lombok.*;
import java.time.LocalDate;

/**
 * 用户基本信息持久化对象
 * 该类用于存储用户的基本信息，包括用户ID、用户名、密码、性别、头像、电话号码以及创建和更新时间。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasePO {
    /**
     * 用户ID
     * 唯一标识每个用户的主键
     */
    public Integer id;

    /**
     * 用户名
     * 用户登录时使用的用户名
     */
    public String username;

    /**
     * 用户密码
     * 用户登录时使用的密码，建议使用加密存储
     */
    public String password;

    /**
     * 用户性别
     * 用户的性别信息，true表示男性，false表示女性
     */
    public Boolean sex;

    /**
     * 用户头像URL
     * 用户头像的图片地址
     */
    public String avatar;

    /**
     * 用户电话号码
     * 用户的联系电话
     */
    public String telephone;

    /**
     * 创建时间
     * 记录用户信息首次创建的时间
     */
    public LocalDate createTime;

    /**
     * 更新时间
     * 记录用户信息最后更新的时间
     */
    public LocalDate updateTime;

    /**
     * 初始化创建时间和更新时间
     * 该方法用于在创建用户信息时初始化创建时间和更新时间为当前时间
     */
    public void initTime() {
        this.createTime = LocalDate.now();
        this.updateTime = LocalDate.now();
    }

    public void initUpdateDate() {
        this.updateTime = LocalDate.now();
    }
}