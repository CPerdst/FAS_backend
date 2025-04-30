package com.l1Akr.pojo.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "样本报告DTO")
public class SampleReportDTO {
    @Schema(description = "主键")
    Integer id; // 主键

    @Schema(description = "样本哈希")
    String fileMd5; // 样本md5

    @Schema(description = "样本PDF报告路径")
    String pdfPath; // pdf报告OSS路径

    @Schema(description = "样本PDF报告大小")
    Long pdfSize; // pdf报告大小

    @Schema(description = "样本PDF报告创建时间")
    LocalDateTime pdfCreateTime; // pdf报告创建时间
}
