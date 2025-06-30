package com.l1Akr.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAddDTO {

    private String name;
    
    private String description;
    
    private Boolean deletable = true;
}