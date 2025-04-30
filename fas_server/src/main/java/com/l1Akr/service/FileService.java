package com.l1Akr.service;

import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileService {

    /**
     * 用户上传头像
     * @param file
     * @return
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 用户上传样本
     * @param file
     * @return
     */
    boolean uploadSample(MultipartFile file, int userId);

    /**
     * 根据用户id获取样本列表
     * @param userId
     * @return
     */
    PageInfo<SampleBaseLightDTO> getSampleListByUserId(int userId, int pageNum, int pageSize);

    /**
     * 批量上传样本
     * @param file
     * @return
     */
    boolean batchUploadSamples(MultipartFile file);

}
