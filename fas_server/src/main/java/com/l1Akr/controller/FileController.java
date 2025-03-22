package com.l1Akr.controller;

import com.l1Akr.common.exceptionss.FileUploadFailedException;
import com.l1Akr.common.exceptionss.MyBatisException;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.utils.JwtUtils;
import com.l1Akr.common.utils.OssUtils;
import com.l1Akr.common.utils.UserThreadLocal;
import com.l1Akr.dao.UserDAO;
import com.l1Akr.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/file")
@Tag(name = "文件管理模块", description = "文件/样本/头像上传下载")
public class FileController {

    @Autowired
    public UserService userService;

    @Autowired
    public JwtUtils jwtUtils;

    @Autowired
    public OssUtils ossUtils;

    @Operation(summary = "用户上传头像")
    @PostMapping("/avatar/upload")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String s;
        try {
            // 校验文件
            if(!isValidFile(file)) {
                return Result.error("仅支持JPG/PNG格式且不超过2MB");
            }

            // 生成唯一文件名
            UserDAO user = UserThreadLocal.getCurrentUser();
            String userId = user.getId().toString();
            String fileName = ossUtils.generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()), userId);

            // 上传到Oss
            s = ossUtils.uploadAvatar(file, fileName);
            if (s == null) {
                return Result.error("上传失败，请重试");
            }

            // 更新数据库
            UserDAO newUser = new UserDAO();
            newUser.setId(user.getId());
            newUser.setAvatar(s);
            newUser.setUpdateTime(new Date());
            userService.updateUser(newUser);
        } catch (IOException e) {
            log.error("文件上传失败", e); // 记录日志
            throw new FileUploadFailedException();
        } catch (MyBatisSystemException e) {
            log.error("MyBatis异常", e);
            throw new MyBatisException();
        }

        return Result.success(s);
    }

    @Operation(summary = "根据用户id获取用户头像地址")
    @GetMapping("/avatar")
    public Result<String> avatarGet() {
        return Result.success(userService.getAvatarById(UserThreadLocal.getCurrentUser().getId().toString()));
    }

    // 文件校验方法
    private boolean isValidFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            return false;
        }
        return file.getSize() <= 2 * 1024 * 1024; // 2MB限制
    }


}
