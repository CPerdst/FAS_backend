package com.l1Akr.service;

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
     * 批量上传样本
     * @param file
     * @return
     */
    boolean batchUploadSamples(MultipartFile file);

}
