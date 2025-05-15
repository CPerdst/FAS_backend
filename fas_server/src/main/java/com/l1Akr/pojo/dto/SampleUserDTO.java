package com.l1Akr.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "样本用户信息DTO")
public class SampleUserDTO {
    // 样本信息
    @Schema(description = "样本ID")
    private Integer id;
    
    @Schema(description = "样本名称")
    private String filename;
    
    @Schema(description = "文件后缀")
    private String fileExt;
    
    @Schema(description = "文件大小")
    private Long fileSize;
    
    @Schema(description = "文件MD5")
    private String fileMd5;
    
    @Schema(description = "文件类型")
    private Integer fileType;
    
    @Schema(description = "文件描述")
    private String fileDesc;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "样本处理状态")
    private Integer disposeStatus;
    
    @Schema(description = "PDF报告路径")
    private String pdfPath;
    
    @Schema(description = "PDF报告创建时间")
    private LocalDateTime pdfCreateTime;
    
    // 用户信息
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "用户头像")
    private String avatar;
} 