package com.l1Akr.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleHistoryDTO {

    @Schema(description = "历史样本总数")
    Long totalCount;

    @Schema(description = "未处理样本总数")
    Long undisposedCount;

    @Schema(description = "处理中样本总数")
    Long disposingCount;

    @Schema(description = "安全样本总数")
    Long safeCount;

    @Schema(description = "危险样本总数")
    Long virusCount;

    @Schema(description = "处理失败样本总数")
    Long failedCount;

}
