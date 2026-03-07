package org.example.warehouseservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ItemImageService {
    List<String> uploadItemImages(Long itemId, List<MultipartFile> files) throws IOException;
    void deleteItemImage(Long imageId);
}
