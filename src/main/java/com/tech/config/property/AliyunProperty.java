package com.tech.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云配置
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProperty {
    private String accessKeyId;
    private String accessKeySecret;
    private String ossUrl;
    private String ossEndpoint;
    private String ossRegion;
    private String ossBucket;

    public String getOssUrl() {
        return String.format(ossUrl, ossBucket);
    }
}
