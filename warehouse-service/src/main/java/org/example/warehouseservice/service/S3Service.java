package org.example.warehouseservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {
    List<String> upload(List<MultipartFile> files, String keyPrefix) throws IOException;
    void delete(String objectKey);
}
