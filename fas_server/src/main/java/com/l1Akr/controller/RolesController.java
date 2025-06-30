package com.l1Akr.controller;

import com.github.pagehelper.PageInfo;
import com.l1Akr.annotation.RequiredPermission;
import com.l1Akr.common.result.Result;
import com.l1Akr.pojo.dto.RoleAddDTO;
import com.l1Akr.pojo.dto.RoleUpdateDTO;
import com.l1Akr.pojo.po.RolePO;
import com.l1Akr.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/role")
@Tag(name = "角色管理模块", description = "角色的CURD操作")
public class RolesController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "获取角色列表（分页）")
    @GetMapping("/list")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<PageInfo<RolePO>> getAllRoles(
            @RequestParam(defaultValue = "1") @Parameter(name = "pageNum", description = "页码") Integer pageNum,
            @RequestParam(defaultValue = "10") @Parameter(name = "pageSize", description = "页长") Integer pageSize) {
        if(pageNum < 1 || pageSize < 1 || pageNum > 100 || pageSize > 100) {
            return new Result<>(Result.ResultEnum.PAGE_NUM_OR_SIZE_ERROR);
        }
        PageInfo<RolePO> pageInfo = roleService.getAllRoles(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @Operation(summary = "添加角色")
    @PostMapping("/add")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> addRole(@RequestBody RoleAddDTO roleAddDTO) {
        roleService.addRole(roleAddDTO);
        return Result.success("角色添加成功");
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/delete/{id}")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return Result.success("角色删除成功");
    }

    @Operation(summary = "更新角色信息")
    @PutMapping("/update")
    @RequiredPermission(roles = {"ADMIN"})
    public Result<String> updateRole(@RequestBody RoleUpdateDTO roleUpdateDTO) {
        roleService.updateRole(roleUpdateDTO);
        return Result.success("角色信息更新成功");
    }
}