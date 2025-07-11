package com.l1Akr.controller;

import com.l1Akr.annotation.RequiredPermission;
import com.l1Akr.pojo.dto.SampleHistoryDTO;
import com.l1Akr.pojo.dto.SampleLineHistoryDTO;
import com.l1Akr.pojo.dto.SampleUserDTO;
import com.l1Akr.service.SampleService;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageInfo;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.util.UserThreadLocal;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@Tag(name = "样本管理模块", description = "样本列表查询")
@RequestMapping("/sample")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    /**
     * 根据用户id查询样本列表，最小化查询条件，最少化字段返回
     * 只返回样本id、样本名称、样本大小、上传时间、样本哈希
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Operation(summary = "根据用户查询样本（分页）")
    @GetMapping("/list")
    @RequiredPermission(permissions = "sample:select", roles = {"ADMIN", "USER"})
    public Result<PageInfo<SampleBaseLightDTO>> sampleList(
            @RequestParam(defaultValue = "1") @Parameter(name = "pageNum", description = "页码") Integer pageNum,
            @RequestParam(defaultValue = "10") @Parameter(name = "pageSize", description = "页长") Integer pageSize) {
        log.info("pageNum: {}, pageSize: {}", pageNum, pageSize);
        // 如果页码小于1，则返回错误信息
        if(pageNum < 1 || pageSize < 1) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        // 如果页码大于100，则返回错误信息
        if(pageNum > 100 || pageSize > 100) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        PageInfo<SampleBaseLightDTO> pageInfo = sampleService.getSampleListByUserId(
                UserThreadLocal.getCurrentUser().getUserBase().getId(),
                pageNum,
                pageSize
        );
        return Result.success(pageInfo);
    }

    /**
     * 查询用户样本历史记录
     * @return
     */
    @Operation(summary = "查询用户样本历史记录")
    @GetMapping("/allHistory")
    @RequiredPermission(permissions = "sample:select", roles = {"ADMIN", "USER"})
    public Result<SampleHistoryDTO> getAllHistory() {
        log.info("user {} get allHistory",UserThreadLocal.getCurrentUser().getUserBase().getId());
        SampleHistoryDTO sampleHistoryByUserId = sampleService.getAllSampleHistoryByUserId(
                UserThreadLocal.getCurrentUser().getUserBase().getId()
        );
        return Result.success(sampleHistoryByUserId);
    }

    /**
     * 查询用户近期样本提交历史数据
     */
    @Operation(summary = "查询用户近期N天内样本提交历史数据")
    @GetMapping("/lineHistory")
    @RequiredPermission(permissions = "sample:select", roles = {"ADMIN", "USER"})
    public Result<List<SampleLineHistoryDTO>> getLineHistory(
            @RequestParam(defaultValue = "30") @Parameter(name = "days", description = "天数") Integer days
    ) {
        log.info("user {} get lineHistory {}",UserThreadLocal.getCurrentUser().getUserBase().getId(), days);
        List<Integer> daysList = Arrays.asList(7, 30, 90);
        if(!daysList.contains(days)) {
            return new Result<>(Result.ResultEnum.PARAM_ERROR);
        }
        return Result.success(sampleService.getLineSampleHistoryByUserId(
                UserThreadLocal.getCurrentUser().getUserBase().getId(),
                days
        ));
    }
    
    @Operation(summary = "获取样本总数")
    @GetMapping("/totalCount")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<Integer> getSampleTotalCount() {
        return Result.success(sampleService.getSampleTotalCount());
    }
    
    @Operation(summary = "获取报告总数")
    @GetMapping("/reportCount")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<Integer> getReportTotalCount() {
        return Result.success(sampleService.getReportTotalCount());
    }

    /**
     * 管理员查询所有样本列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 样本列表，包含上传用户基本信息
     */
    @Operation(summary = "管理员查询所有样本列表（分页）")
    @GetMapping("/admin/list")
    @RequiredPermission(permissions = "sample:manage", roles = {"ADMIN"})
    public Result<PageInfo<SampleUserDTO>> getAllSamples(
            @RequestParam(defaultValue = "1") @Parameter(name = "pageNum", description = "页码") Integer pageNum,
            @RequestParam(defaultValue = "10") @Parameter(name = "pageSize", description = "页长") Integer pageSize) {
        log.info("管理员查询所有样本列表 pageNum: {}, pageSize: {}", pageNum, pageSize);
        
        // 参数检查
        if(pageNum < 1 || pageSize < 1 || pageNum > 100 || pageSize > 100) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        
        PageInfo<SampleUserDTO> pageInfo = sampleService.getAllSamples(pageNum, pageSize);
        return Result.success(pageInfo);
    }
    
    /**
     * 删除样本
     * @param id 样本ID
     * @return 操作结果
     */
    @Operation(summary = "删除样本")
    @DeleteMapping("/delete/{id}")
    @RequiredPermission(permissions = "sample:delete", roles = {"ADMIN"})
    public Result<String> deleteSample(
            @PathVariable @Parameter(name = "id", description = "样本ID") Integer id) {
        log.info("删除样本 id: {}", id);
        
        if (id == null || id <= 0) {
            return new Result<>(Result.ResultEnum.PARAM_ERROR);
        }
        
        sampleService.deleteSample(id);
        return Result.success("删除成功");
    }
    
    /**
     * 获取样本详情
     * @param id 样本ID
     * @return 样本详情，包含上传用户基本信息
     */
    @Operation(summary = "获取样本详情")
    @GetMapping("/{id}")
    @RequiredPermission(permissions = "sample:select", roles = {"ADMIN"})
    public Result<SampleUserDTO> getSampleDetail(
            @PathVariable @Parameter(name = "id", description = "样本ID") Integer id) {
        log.info("获取样本详情 id: {}", id);
        
        if (id == null || id <= 0) {
            return new Result<>(Result.ResultEnum.PARAM_ERROR);
        }
        
        SampleUserDTO sample = sampleService.getSampleDetail(id);
        return Result.success(sample);
    }
}
