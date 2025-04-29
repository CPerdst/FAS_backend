package com.l1Akr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "样本基本信息DTO")
public class SampleBaseLightDTO {
    @Schema(description = "主键")
    Integer id; // 主键

    @Schema(description = "样本名")
    String filename; // 样本名

    @Schema(description = "样本大小")
    Long fileSize; // 文件大小

    @Schema(description = "样本哈希")
    String fileMd5; // 文件md5

    @Schema(description = "样本当前状态")
    Integer disposeStatus; // 样本处理状态 1-未处理 2-处理中 3-未发现病毒 4-发现病毒 5-处理失败

    @Schema(description = "样本创建时间")
    LocalDateTime createTime; // 创建时间
}
