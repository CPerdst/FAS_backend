package com.l1Akr.controller;

import com.github.pagehelper.PageInfo;
import com.l1Akr.common.exceptionss.BusinessException;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.utils.JwtUtils;
import com.l1Akr.common.utils.OssUtils;
import com.l1Akr.common.utils.UserThreadLocal;
import com.l1Akr.po.SampleBasePO;
import com.l1Akr.po.UserBasePO;
import com.l1Akr.service.FileService;
import com.l1Akr.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.Objects;

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
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
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
    public Result<String> avatarGet() {
        return Result.success(userService.getAvatarById(UserThreadLocal.getCurrentUser().getId().toString()));
    }

    /**
     * 用户上传样本
     * @param file
     * @return
     */
    @Operation(summary = "用户上传样本")
    @PostMapping("/sample/upload")
    public Result<String> sampleUpload(@RequestParam("file") MultipartFile file) {
        // 如果上传的文件为空，则返回错误信息
        if(isUnValidFile(file, null, MAX_FILE_UPLOAD_SIZE)) {
            throw new BusinessException(Result.ResultEnum.UPLOAD_FORMAT_LIMIT_EXCEEDED_50MB);
        }
        // 样本文件没有问题的话，上传样本文件
        boolean success = fileService.uploadSample(file, UserThreadLocal.getCurrentUser().getId());
        return (success ? Result.success("上传成功") : Result.error("上传失败"));
    }

    @Operation(summary = "根据用户查询样本（分页）")
    @GetMapping("/sample/list")
    public Result<PageInfo<SampleBasePO>> sampleList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo<SampleBasePO> pageInfo = fileService.getSampleListByUserId(
                UserThreadLocal.getCurrentUser().getId(),
                pageNum,
                pageSize
        );
        return Result.success(pageInfo);
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
        if(!CollectionUtils.isEmpty(typeArray) && !StringUtils.isEmpty(contentType)) {
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
