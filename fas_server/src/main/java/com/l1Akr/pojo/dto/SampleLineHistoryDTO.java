package com.l1Akr.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleLineHistoryDTO {

    @Schema(description = "日期")
    LocalDate date;

    @Schema(description = "样本数")
    Long total;

}
