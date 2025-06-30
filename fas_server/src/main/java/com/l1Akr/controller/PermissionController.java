package com.l1Akr.controller;

import com.github.pagehelper.PageInfo;
import com.l1Akr.annotation.RequiredPermission;
import com.l1Akr.common.result.Result;
import com.l1Akr.pojo.dto.PermissionAddDTO;
import com.l1Akr.pojo.dto.PermissionUpdateDTO;
import com.l1Akr.pojo.po.PermissionPO;
import com.l1Akr.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/permission")
@Tag(name = "权限管理模块", description = "权限的CURD操作")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "获取权限列表（分页）")
    @GetMapping("/list")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<PageInfo<PermissionPO>> getAllPermissions(
            @RequestParam(defaultValue = "1") @Parameter(name = "pageNum", description = "页码") Integer pageNum,
            @RequestParam(defaultValue = "10") @Parameter(name = "pageSize", description = "页长") Integer pageSize) {
        if(pageNum < 1 || pageSize < 1 || pageNum > 100 || pageSize > 100) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        PageInfo<PermissionPO> pageInfo = permissionService.getAllPermissions(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @Operation(summary = "添加权限")
    @PostMapping("/add")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> addPermission(@RequestBody PermissionAddDTO permissionAddDTO) {
        permissionService.addPermission(permissionAddDTO);
        return Result.success("权限添加成功");
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/delete/{id}")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> deletePermission(@PathVariable Integer id) {
        permissionService.deletePermission(id);
        return Result.success("权限删除成功");
    }

    @Operation(summary = "更新权限信息")
    @PutMapping("/update")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> updatePermission(@RequestBody PermissionUpdateDTO permissionUpdateDTO) {
        permissionService.updatePermission(permissionUpdateDTO);
        return Result.success("权限信息更新成功");
    }
} 