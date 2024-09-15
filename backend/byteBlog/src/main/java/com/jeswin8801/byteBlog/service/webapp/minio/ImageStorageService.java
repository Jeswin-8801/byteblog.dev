package com.jeswin8801.byteBlog.service.webapp.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

@Service
public class ImageStorageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    public String uploadImage(MultipartFile file) {
        String fileName = generateFileName(file);
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName).stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return minioUrl + "/" + bucketName + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store image file.", e);
        }
    }

    private String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");
    }
}