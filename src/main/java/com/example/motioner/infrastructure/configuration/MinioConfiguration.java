package com.example.motioner.infrastructure.configuration;

import com.example.motioner.infrastructure.MinioFileManager;
import com.example.motioner.presentation.VideoResponseFactory;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Value("${minio.host}")
    private String host;

    @Value("${minio.port}")
    private String port;

    @Value("${minio.access.key}")
    private String accessKey;

    @Value("${minio.secret.key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    @Bean
    public MinioClient getClient() {
        return MinioClient.builder()
                .endpoint(host, Integer.parseInt(port), false)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public VideoResponseFactory getResponseFactory() {
        return new VideoResponseFactory();
    }

    @Bean
    public MinioFileManager getManager(MinioClient client) {
        return new MinioFileManager(client);
    }
}
