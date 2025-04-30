package com.l1Akr.service.implement;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.util.OssUtils;
import com.l1Akr.common.util.ShaUtils;
import com.l1Akr.common.util.UserThreadLocal;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;
import com.l1Akr.manager.SampleCheckManager;
import com.l1Akr.mapper.FileMapper;
import com.l1Akr.mapper.UserSampleMappingMapper;
import com.l1Akr.pojo.po.SampleBasePO;
import com.l1Akr.pojo.po.UserBasePO;
import com.l1Akr.pojo.po.UserSampleMappingPO;
import com.l1Akr.service.FileService;
import com.l1Akr.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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

    public FileServiceImpl(OssUtils ossUtils,
                           UserService userService,
                           FileMapper fileMapper,
                           UserSampleMappingMapper userSampleMappingMapper, SampleCheckManager sampleCheckManager) {
        this.ossUtils = ossUtils;
        this.userService = userService;
        this.fileMapper = fileMapper;
        this.userSampleMappingMapper = userSampleMappingMapper;
        this.sampleCheckManager = sampleCheckManager;
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        // 生成唯一文件名
        UserBasePO user = UserThreadLocal.getCurrentUser();
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

        // 创建用户-样本映射
        UserSampleMappingPO userSampleMappingPO = new UserSampleMappingPO();
        userSampleMappingPO.setUserId(userId);
        userSampleMappingPO.setSampleId(sampleBasePO.getId());
        userSampleMappingMapper.insertByUserSampleMappingPo(userSampleMappingPO);
        log.info("File {} upload to OSS success, url: {}", filename, ossUrlPath);

        // 将该样本提交检测
        sampleCheckManager.addSampleToQueue(sampleBasePO);
        log.info("sample {} was added to checkQueue", filename);

        return true;
    }

    @Override
    public boolean batchUploadSamples(MultipartFile file) {
        return false;
    }

    /**
     * 根据用户id获取样本列表
     * @param userId
     * @return
     */
    @Override
    public PageInfo<SampleBaseLightDTO> getSampleListByUserId(int userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SampleBaseLightDTO> sampleBaseLightList = fileMapper.selectSamplesByUserId(userId).stream().map(sampleBasePO -> {
                    SampleBaseLightDTO sampleBaseLightDTO = new SampleBaseLightDTO();
                    sampleBaseLightDTO.setId(sampleBasePO.getId());
                    sampleBaseLightDTO.setFilename(sampleBasePO.getFilename());
                    sampleBaseLightDTO.setFileSize(sampleBasePO.getFileSize());
                    sampleBaseLightDTO.setFileMd5(sampleBasePO.getFileMd5());
                    sampleBaseLightDTO.setCreateTime(sampleBasePO.getCreateTime());
                    sampleBaseLightDTO.setDisposeStatus(sampleBasePO.getDisposeStatus());
                    return sampleBaseLightDTO;
                }).toList();
        return new PageInfo<>(sampleBaseLightList);
    }
}
