package com.l1Akr.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.l1Akr.common.config.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Slf4j
public class OssUtils {

    @Autowired
    private OssProperties ossProperties;

    public String uploadAvatar(MultipartFile file, String filename) throws IOException {
        log.info("OssProperties: {}", ossProperties);
        // 初始化Oss客户端
        OSS ossClient = new OSSClientBuilder()
                .build(
                        ossProperties.getEndpoint()
                        , ossProperties.getAccessKeyId()
                        , ossProperties.getAccessKeySecret()
                );

        try {
            // 生成文件上传路径
            String ossPath = "avatar/" + filename;

            // 将文件上传到Oss
            ossClient.putObject(ossProperties.getBucketName(), ossPath, file.getInputStream());

            // 生成公开访问路径
            return "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + ossPath;
        } finally {
            ossClient.shutdown();
        }

    }
    // 生成唯一文件名（用户ID+时间戳+扩展名）
    public String generateUniqueFileName(String originalFilename, String userId) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        return userId + "/" + System.currentTimeMillis() + "." + fileExtension;
    }


}
