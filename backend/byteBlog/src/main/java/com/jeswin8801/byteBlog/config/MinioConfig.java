package com.jeswin8801.byteBlog.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${minio.credentials.username}")
    private String username;

    @Value("${minio.credentials.password}")
    private String password;

    @Value("${minio.url}")
    private String url;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(username, password)
                .build();
    }
}
