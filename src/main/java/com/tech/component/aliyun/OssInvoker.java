package com.tech.component.aliyun;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.tech.common.enums.ErrorCode;
import com.tech.common.enums.FileTypeEnum;
import com.tech.config.property.AliyunProperty;
import com.tech.config.response.bean.BizException;
import com.tech.util.IdUtil;
import com.tech.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssInvoker {

    private final AliyunProperty property;
    private OSS ossClient;

    @PostConstruct
    public void init() {
        CredentialsProvider credentialsProvider =
                new DefaultCredentialProvider(property.getAccessKeyId(), property.getAccessKeySecret());
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        ossClient = OSSClientBuilder.create()
                .endpoint(property.getOssEndpoint())
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(property.getOssRegion())
                .build();
    }

    public String upload(String fileExtension, Integer fileType, Map<String, Object> headers, String base64Data) {
        byte[] data = Base64.getDecoder().decode(base64Data.getBytes(StandardCharsets.UTF_8));
        return this.upload(fileExtension, fileType, headers, data);
    }

    @SneakyThrows
    public String upload(String fileExtension, Integer fileType, String url) {
        try (InputStream inputStream = new URL(url).openStream()) {
            return upload(fileExtension, fileType, inputStream.readAllBytes());
        }
    }

    public String upload(String fileExtension, Integer fileType, byte[] data) {
        FileTypeEnum fileTypeEnum = FileTypeEnum.getEnum(fileType);
        if (fileTypeEnum == null) {
            return "";
        }
        String objectName = fileTypeEnum.getPrefix() + "/" + IdUtil.uuid() + "." + fileExtension;
        return this.upload(property.getOssBucket(), objectName, data);
    }

    public String upload(String fileExtension, Integer fileType, Map<String, Object> headers, byte[] data) {
        FileTypeEnum fileTypeEnum = FileTypeEnum.getEnum(fileType);
        if (fileTypeEnum == null) {
            return "";
        }
        String objectName = fileTypeEnum.getPrefix() + "/" + IdUtil.uuid() + "." + fileExtension;
        return this.upload(property.getOssBucket(), objectName, headers, data);
    }

    public String upload(String bucketName, String objectName, byte[] data) {
        if (StringUtils.isBlank(bucketName) || StringUtils.isBlank(objectName) || data == null || data.length == 0) {
            return "";
        }
        PutObjectResult result = ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(data));
        log.info("oss upload binary data, result:{}", JsonUtil.toJson(result));
        return property.getOssUrl() + objectName;
    }

    public String upload(String bucketName, String objectName, Map<String, Object> headers, byte[] data) {
        if (StringUtils.isBlank(bucketName)
                || StringUtils.isBlank(objectName)
                || CollectionUtils.isEmpty(headers)
                || data == null || data.length == 0) {
            return "";
        }
        ObjectMetadata metadata = new ObjectMetadata();
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            metadata.setHeader(entry.getKey(), entry.getValue());
        }
        PutObjectResult result = ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(data), metadata);
        log.info("oss upload binary data, result={}", JsonUtil.toJson(result));
        return property.getOssUrl() + objectName;
    }

    public String upload(MultipartFile file) {
        if (file == null) {
            return "";
        }
        String objectName = "default/";
        String contentType = file.getContentType();
        if (StringUtils.isNotBlank(contentType)) {
            if (contentType.startsWith(FileTypeEnum.IMAGE.getPrefix())) {
                objectName = FileTypeEnum.IMAGE.getPrefix();
            } else if (contentType.startsWith(FileTypeEnum.AUDIO.getPrefix())) {
                objectName = FileTypeEnum.AUDIO.getPrefix();
            } else if (contentType.startsWith(FileTypeEnum.VIDEO.getPrefix())) {
                objectName = FileTypeEnum.VIDEO.getPrefix();
            }
        }
        objectName = objectName + "/" + IdUtil.uuid() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            return this.upload(property.getOssBucket(), objectName, file.getBytes());
        } catch (IOException e) {
            log.error("oss upload error", e);
            throw new BizException(ErrorCode.ALIYUN_ERROR1);
        }
    }

    public void delete(String url) {
        if (StringUtils.isBlank(url)) {
            log.error("删除OSS数据，参数为空");
            return;
        }
        String objectName = url.substring(property.getOssUrl().length());
        ossClient.deleteObject(property.getOssBucket(), objectName);
        log.info("删除OSS数据, bucket:{}, objectName:{}", property.getOssBucket(), objectName);
    }

    @Async
    public void deleteAsync(String url) {
        if (StringUtils.isNotBlank(url)) {
            this.delete(url);
        }
    }
}
