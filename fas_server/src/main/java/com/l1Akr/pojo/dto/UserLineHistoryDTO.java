package com.l1Akr.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLineHistoryDTO {

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "用户创建数量")
    private Long createCount;
    
    @Schema(description = "用户更新数量")
    private Long updateCount;
} 