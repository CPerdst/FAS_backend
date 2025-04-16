package com.l1Akr.service.implement;

import com.l1Akr.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {


    @Override
    public boolean uploadAvatar(MultipartFile file) {
        return false;
    }

    @Override
    public boolean uploadSample(MultipartFile file) {
        // 获取该样本的元数据
        String filename = file.getOriginalFilename();
        long filesize = file.getSize();

        return false;
    }

    @Override
    public boolean batchUploadSamples(MultipartFile file) {
        return false;
    }


}
