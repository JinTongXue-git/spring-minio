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

    /**配置*/
    @Bean
    public MinioClient minioClient(){
        MinioClient minioadmin = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        String[] buckets = new String[]{"images-bucket" , "contract-bucket"};
        try {
            for (String bucketName : buckets) {
                if (!minioadmin.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()) ){
                    minioadmin.makeBucket(
                            MakeBucketArgs.builder().bucket(bucketName).build()
                    );
                }
            }
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
        //minioadmin.setTimeout(1000, 1000, 1000); // Set connect and read timeout to 1000 milliseconds each

        return minioadmin;
    }




}
