package com.l1Akr.controller;

import com.l1Akr.annotation.RequiredPermission;
import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.util.UserThreadLocal;
import com.l1Akr.service.FileService;
import com.l1Akr.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/file")
@Tag(name = "文件管理模块", description = "文件/样本/头像上传下载")
public class FileController {

    @Autowired
    public UserService userService;

    @Autowired
    public FileService fileService;

    private final long MAX_FILE_UPLOAD_SIZE = 50 * 1024 * 1024; // 50MB限制

    private final long MAX_AVATAR_UPLOAD_SIZE = 2 * 1024 * 1024; // 2MB限制

    private final List<String> avatarEnabledTypeArray = Arrays.asList("image/jpeg", "image/png");

    /**
     * 用户上传头像
     * @param file
     * @return
     */
    @Operation(summary = "用户上传头像")
    @PostMapping("/avatar/upload")
    @RequiredPermission(permissions = {"user:update"}, roles = {"ADMIN", "USER"})
    public Result<String> uploadAvatar(@RequestParam("file") @Parameter(name = "file", description = "头像文件") MultipartFile file) {
        // 校验文件
        if(isUnValidFile(file, avatarEnabledTypeArray, MAX_AVATAR_UPLOAD_SIZE)) {
            throw new BusinessException(Result.ResultEnum.UPLOAD_FORMAT_LIMIT_EXCEEDED);
        }
        // 上传头像并获取头像地址
        String s = fileService.uploadAvatar(file);
        return Result.success(s);
    }

    /**
     * 根据用户id获取用户头像地址
     * @return
     */
    @Operation(summary = "根据用户id获取用户头像地址")
    @GetMapping("/avatar")
    @RequiredPermission(permissions = "user:select", roles = {"ADMIN", "USER"})
    public Result<String> avatarGet() {
        return Result.success(userService.getAvatarById(UserThreadLocal.getCurrentUser().getUserBase().getId().toString()));
    }

    /**
     * 用户上传样本
     * @param file
     * @return
     */
    @Operation(summary = "用户上传样本")
    @PostMapping("/sample/upload")
    @RequiredPermission(permissions = "sample:upload", roles = {"ADMIN", "USER"})
    public Result<String> sampleUpload(@RequestParam("file") MultipartFile file) {
        // 如果上传的文件为空，则返回错误信息
        if(isUnValidFile(file, null, MAX_FILE_UPLOAD_SIZE)) {
            throw new BusinessException(Result.ResultEnum.UPLOAD_FORMAT_LIMIT_EXCEEDED_50MB);
        }
        // 样本文件没有问题的话，上传样本文件
        boolean success = fileService.uploadSample(file, UserThreadLocal.getCurrentUser().getUserBase().getId());
        return (success ? Result.success("上传成功") : Result.error("上传失败"));
    }

    /**
     * 文件校验方法
     * @param file
     * @param typeArray
     * @param maxFileSize
     * @return
     */
    private boolean isUnValidFile(MultipartFile file, List<String> typeArray, long maxFileSize) {
        String contentType = file.getContentType();
        boolean flag = true;
        if(ObjectUtils.isEmpty(file) || file.isEmpty()) {
            return true;
        }
        if(!CollectionUtils.isEmpty(typeArray) && !StringUtils.isBlank(contentType)) {
            flag = false;
            for(String type : typeArray) {
                if(contentType.contains(type)) {
                    flag = true;
                }
            }
        }
        if(!flag) {
            return true;
        }
        return file.getSize() > maxFileSize; // 2MB限制
    }

}
