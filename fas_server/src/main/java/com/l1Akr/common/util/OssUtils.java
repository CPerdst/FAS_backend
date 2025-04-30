package com.l1Akr.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.l1Akr.common.config.OssProperties;
import com.l1Akr.pojo.po.SampleBasePO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;

@Component
@Slf4j
public class OssUtils {

    @Autowired
    private OssProperties ossProperties;

    private OSS ossClient;

    private static final String AVATAR_UPLOAD_PATH = "avatar/";

    private static final String SAMPLE_UPLOAD_PATH = "sample/";

    private static final String PDF_REPORT_UPLOAD_PATH = "report/";

    @PostConstruct
    public void init() {
        log.info("OssProperties: {}", ossProperties);
        ossClient = new OSSClientBuilder()
                .build(
                        ossProperties.getEndpoint()
                        , ossProperties.getAccessKeyId()
                        , ossProperties.getAccessKeySecret()
                );
        log.info("OSSClient init success");
    }

    /**
     * 上传头像
     * @param file
     * @param filename
     * @return
     * @throws IOException
     */
    public String uploadAvatar(MultipartFile file, String filename) {
        String ossPath = AVATAR_UPLOAD_PATH + filename;
        String ossUrlPath;
        try {
            ossUrlPath = uploadFile(file.getInputStream(), ossPath);
        } catch (IOException e) {
            log.error("upload avatar failed: {}", e.getMessage());
            return "";
        }
        return StringUtils.isEmpty(ossUrlPath) ? "" : ossUrlPath;
    }

    /**
     * 上传样本
     * 将样本上传到 "/sample/{userId}/{date}/{filename}"
     * @param file
     * @param filename
     * @return
     * @throws IOException
     */
    public String uploadSample(MultipartFile file, String filename) throws IOException {
        String ossPath = SAMPLE_UPLOAD_PATH + filename;
        String ossUrlPath;
        try {
            ossUrlPath = uploadFile(file.getInputStream(), ossPath);
        } catch (IOException e) {
            log.error("upload sample failed: {}", e.getMessage());
            return "";
        }
        return StringUtils.isEmpty(ossUrlPath) ? "" : ossUrlPath;
    }

    public String uploadPDFReport(InputStream inputStream, String filename) throws IOException {
        String ossPath = PDF_REPORT_UPLOAD_PATH + filename;
        String ossUrlPath;
        ossUrlPath = uploadFile(inputStream, ossPath);
        return StringUtils.isEmpty(ossUrlPath) ? "" : ossUrlPath;
    }

    /**
     * 上传文件
     * @param is
     * @param ossPath
     * @return
     */
    private String uploadFile(InputStream is, String ossPath) {
        PutObjectResult putObjectResult = ossClient.putObject(ossProperties.getBucketName(), ossPath, is);
        return putObjectResult != null ? "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + ossPath : "";
    }

    /**
     * 生成唯一文件名（用户ID+时间戳+扩展名）
     * @param originalFilename
     * @param userId
     * @return
     */
    public String generateUniqueFileNameForAvatar(String originalFilename, String userId) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        return userId + "/" + System.currentTimeMillis() + "." + fileExtension;
    }

    public String generateUniqueFileNameForSample(SampleBasePO sampleBasePO, String userId) {
        String fileExtension = sampleBasePO.getFileExt();
        return userId
                + "/" + sampleBasePO.getCreateTime().atZone(ZoneId.systemDefault()).toLocalDate()
                + "/" + sampleBasePO.getFileMd5() + "-" + System.currentTimeMillis() + (StringUtils.isBlank(fileExtension) ? "" : "." + fileExtension);
    }

    public String generateUniqueFileNameForPDFReport(SampleBasePO sampleBasePO, String userId) {
        String fileExtension = "pdf";
        return userId
                + "/" + sampleBasePO.getCreateTime().atZone(ZoneId.systemDefault()).toLocalDate()
                + "/" + sampleBasePO.getFileMd5() + "-" + System.currentTimeMillis() + (StringUtils.isBlank(fileExtension) ? "" : "." + fileExtension);
    }

    public void deleteSample(String filePath) {
        ossClient.deleteObject(ossProperties.getBucketName(), filePath);
    }

    public InputStream downloadFile(String filePath) {
        OSSObject object = ossClient.getObject(ossProperties.getBucketName(), filePath);
        return object.getObjectContent();
    }

}
