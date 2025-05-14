package com.l1Akr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.util.UserThreadLocal;
import com.l1Akr.pojo.dto.SampleReportDTO;
import com.l1Akr.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@Slf4j
@Tag(name = "报告管理模块", description = "报告列表查询")
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 根据用户id获取样本报告列表
     * @param userId
     * @return
     */
    @Operation(summary = "根据用户查询报告（分页）")
    @GetMapping("/list")
    public Result<PageInfo<SampleReportDTO>> reportList(
        @RequestParam(defaultValue = "1") @Parameter(name = "pageNum", description = "页码") Integer pageNum,
        @RequestParam(defaultValue = "10") @Parameter(name = "pageSize", description = "页长") Integer pageSize
    ) {
        log.info("pageNum: {}, pageSize: {}", pageNum, pageSize);
        // 如果页码小于1，则返回错误信息
        if(pageNum < 1 || pageSize < 1) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        // 如果页码大于100，则返回错误信息
        if(pageNum > 100 || pageSize > 100) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        PageInfo<SampleReportDTO> pageInfo = reportService.getReportListByUserId(
                UserThreadLocal.getCurrentUser().getUserBase().getId(),
                pageNum,
                pageSize
        );
        return Result.success(pageInfo);
    }

}
