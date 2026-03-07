package org.example.warehouseservice.service.impl;


import org.example.warehouseservice.exception.AppException;
import org.example.warehouseservice.exception.ErrorCode;
import org.example.warehouseservice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.*;

@Service
public class S3ServiceImpl implements S3Service {
    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_FILES = 5;
    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/jpg", "image/png");

    // UPLOAD (1 OR MANY FILES)
    @Override
    public List<String> upload(List<MultipartFile> files, String keyPrefix) throws IOException {

        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.NO_IMAGE_FILES);
        }

        if (files.size() > MAX_FILES) {
            throw new AppException(ErrorCode.MAX_IMAGES_EXCEEDED);
        }

        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {

            validateFile(file);

            String fileName = UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename());
            String key = keyPrefix + "/" + fileName;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            try {
                s3Client.putObject(
                        putRequest,
                        RequestBody.fromBytes(file.getBytes())
                );

                String fileUrl = s3Client.utilities()
                        .getUrl(builder -> builder.bucket(bucketName).key(key))
                        .toExternalForm();

                uploadedUrls.add(fileUrl);

            } catch (Exception e) {
                throw new AppException(ErrorCode.S3_UPLOAD_FAILED);
            }
        }

        return uploadedUrls;
    }

    // DELETE 1 IMAGE
    @Override
    public void delete(String objectKey) {

        if (objectKey == null || objectKey.isBlank()) {
            throw new AppException(ErrorCode.S3_DELETE_FAILED);
        }

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteRequest);

        } catch (Exception e) {
            throw new AppException(ErrorCode.S3_DELETE_FAILED);
        }
    }

    // VALIDATION
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.NO_IMAGE_FILES);
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new AppException(ErrorCode.UNSUPPORTED_FILE_FORMAT);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }
}