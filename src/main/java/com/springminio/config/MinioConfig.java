package com.springminio.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MinioConfig {

    @Value("${minio.endpoint:http://192.168.100.129:9000}")
    private String endpoint;
    @Value("${minio.accessKey:minioadmin}")
    private String accessKey;
    @Value("${minio.secretKey:12345678}")
    private String secretKey;

    /**
     * 配置MinioClient Bean，自动创建桶
     */
//    @Bean(name = "minioClient")
    @Bean
    public MinioClient minioClient(){
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        createBucketsIfNotExists(client, new String[]{"images-bucket", "contract-bucket"});

        return client;
    }

    /**
     * 辅助方法：创建桶（如果不存在）
     */
    private void createBucketsIfNotExists(MinioClient client, String[] bucketNames) {
        try {
            for (String bucketName : bucketNames) {
                if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                    client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("创建MinIO桶失败", e);
        }
    }




}
