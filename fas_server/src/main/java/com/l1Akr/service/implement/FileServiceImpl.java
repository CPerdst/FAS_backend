package com.l1Akr.service.implement;

import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.util.OssUtils;
import com.l1Akr.common.util.ShaUtils;
import com.l1Akr.common.util.UserThreadLocal;
import com.l1Akr.manager.SampleCheckManager;
import com.l1Akr.pojo.dao.mapper.FileMapper;
import com.l1Akr.pojo.dao.mapper.UserSampleMappingMapper;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;
import com.l1Akr.pojo.dto.TaskWithUserSampleDTO;
import com.l1Akr.pojo.po.SampleBasePO;
import com.l1Akr.pojo.po.UserBasePO;
import com.l1Akr.pojo.po.UserSampleMappingPO;
import com.l1Akr.service.FileService;
import com.l1Akr.service.UserService;
import com.l1Akr.websocket.handler.SampleStatusWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final ShaUtils shaUtils = new ShaUtils();
    private final OssUtils ossUtils;
    private final UserService userService;
    private final FileMapper fileMapper;
    private final UserSampleMappingMapper userSampleMappingMapper;
    private final SampleCheckManager sampleCheckManager;
    private final SampleStatusWebSocketHandler sampleStatusWebSocketHandler;

    public FileServiceImpl(OssUtils ossUtils,
                           UserService userService,
                           FileMapper fileMapper,
                           UserSampleMappingMapper userSampleMappingMapper,
                           SampleCheckManager sampleCheckManager,
                           SampleStatusWebSocketHandler sampleStatusWebSocketHandler) {
        this.ossUtils = ossUtils;
        this.userService = userService;
        this.fileMapper = fileMapper;
        this.userSampleMappingMapper = userSampleMappingMapper;
        this.sampleCheckManager = sampleCheckManager;
        this.sampleStatusWebSocketHandler = sampleStatusWebSocketHandler;
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        // 生成唯一文件名
        UserBasePO user = UserThreadLocal.getCurrentUser().getUserBase();
        String userId = user.getId().toString();
        String fileName = ossUtils.generateUniqueFileNameForAvatar(Objects.requireNonNull(file.getOriginalFilename()), userId);

        // 上传到Oss
        String s = ossUtils.uploadAvatar(file, fileName);
        if (StringUtils.isBlank(s)) {
            log.error("avatar upload failed: {}", s);
            throw new BusinessException(Result.ResultEnum.UPLOAD_FAILED);
        }

        // 更新数据库
        UserBasePO newUser = new UserBasePO();
        newUser.setId(user.getId());
        newUser.setAvatar(s);
        newUser.initUpdateDate();
        int affectedRows = userService.updateUserByUserBasePo(newUser);
        if(affectedRows <= 0) {
            throw new BusinessException(Result.ResultEnum.USER_UPDATE_FAILED);
        }

        return s;
    }

    @Override
    public boolean uploadSample(MultipartFile file, int userId) {
        // 获取该样本的元数据
        String filename = file.getOriginalFilename();
        SampleBasePO sampleBasePO = new SampleBasePO();
        sampleBasePO.parseByFile(file);
        sampleBasePO.setDisposeStatus(1); // 默认为未开始处理的状态
        try {
            // 手动设置MD5
            String fileMD5 = shaUtils.MD5(file.getInputStream());
            sampleBasePO.setFileMd5(fileMD5);
        } catch (IOException e) {
            log.info("File {} get MD5 failed as {}", filename, e.getMessage());
            throw new BusinessException(Result.ResultEnum.FILE_MD5_ERROR);
        }

        String ossUrlPath;
        try {
            // 先生成一下文件在OSS中的文件名
            String ossFilename = ossUtils.generateUniqueFileNameForSample(sampleBasePO, String.valueOf(userId));
            // 将样本上传至OSS
            ossUrlPath = ossUtils.uploadSample(file, ossFilename);
            sampleBasePO.setFilePath(ossUrlPath);
        } catch (IOException e) {
            log.info("File {} upload to OSS failed as {}", filename, e.getMessage());
            throw new BusinessException(Result.ResultEnum.UPLOAD_FAILED);
        }

        // 将样本添加至数据库
        fileMapper.insertBySampleBasePo(sampleBasePO);

        // 首先通知当前文件已经被接收到
        SampleBaseLightDTO sampleBaseLightDTO = new SampleBaseLightDTO();
        BeanUtils.copyProperties(sampleBasePO, sampleBaseLightDTO);
        sampleStatusWebSocketHandler.notifyUserWithSampleUploaded(userId, sampleBaseLightDTO);
        // 首先通知用户当前文件的状态为 未开始-1
        sampleStatusWebSocketHandler.notifyUserWithNewStatus(userId, sampleBasePO.getId(), 1);

        // 创建用户-样本映射
        UserSampleMappingPO userSampleMappingPO = new UserSampleMappingPO();
        userSampleMappingPO.setUserId(userId);
        userSampleMappingPO.setSampleId(sampleBasePO.getId());
        userSampleMappingMapper.insertByUserSampleMappingPo(userSampleMappingPO);
        log.info("File {} upload to OSS success, url: {}", filename, ossUrlPath);

        // 将该样本提交检测
        TaskWithUserSampleDTO taskWithUserSampleDTO = new TaskWithUserSampleDTO();
        taskWithUserSampleDTO.setUserId(userId);
        taskWithUserSampleDTO.setSampleBasePO(sampleBasePO);
        sampleCheckManager.addSampleToQueue(taskWithUserSampleDTO);
        log.info("sample {} was added to checkQueue", filename);

        return true;
    }

    @Override
    public boolean batchUploadSamples(MultipartFile file) {
        return false;
    }
}
