package com.l1Akr.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileService {

    boolean uploadAvatar(MultipartFile file);

    boolean uploadSample(MultipartFile file);

    boolean batchUploadSamples(MultipartFile file);

}
