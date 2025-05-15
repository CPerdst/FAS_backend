package com.l1Akr.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateDTO {

    private Integer id;
    
    private String name;

    private String description;
    
    private Boolean deletable = true;
}