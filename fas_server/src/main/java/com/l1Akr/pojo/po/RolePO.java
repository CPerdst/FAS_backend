package com.l1Akr.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePO {
    public Integer id;
    public String name;
    public String description;
    public Boolean deletable;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}