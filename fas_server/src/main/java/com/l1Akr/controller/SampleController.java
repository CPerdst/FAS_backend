package com.l1Akr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.utils.UserThreadLocal;
import com.l1Akr.dto.SampleBaseLightDTO;
import com.l1Akr.service.FileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Tag(name = "样本管理模块", description = "样本列表查询")
@RequestMapping("/sample")
public class SampleController {

    private final FileService fileService;

    public SampleController(FileService fileService) {
        this.fileService = fileService;
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
        PageInfo<SampleBaseLightDTO> pageInfo = fileService.getSampleListByUserId(
                UserThreadLocal.getCurrentUser().getId(),
                pageNum,
                pageSize
        );
        return Result.success(pageInfo);
    }

}
