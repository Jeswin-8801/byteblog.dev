package com.jeswin8801.byteBlog.service.webapp;

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
public class FileStorageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    public String uploadFile(MultipartFile file) {
        String filePath = generateFilePath(file);
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath).stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return minioUrl.replaceFirst("minio", "localhost") + "/" + bucketName + "/" + filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private String generateFilePath(MultipartFile file) {
        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        return ((fileName.endsWith("md") || fileName.endsWith("mdx")) ? "markdown/" : "images/")
                + (new Date().getTime() + "-" + fileName.replace(" ", "_"));
    }
}