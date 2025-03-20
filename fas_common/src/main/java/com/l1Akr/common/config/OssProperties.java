package com.l1Akr.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class OssProperties {

    public String endpoint;
    public String accessKeyId;
    public String accessKeySecret;
    public String bucketName;

}
