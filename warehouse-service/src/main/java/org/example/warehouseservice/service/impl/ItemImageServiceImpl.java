package org.example.warehouseservice.service.impl;

import jakarta.transaction.Transactional;
import org.example.warehouseservice.entity.Item;
import org.example.warehouseservice.entity.ItemImage;
import org.example.warehouseservice.exception.AppException;
import org.example.warehouseservice.exception.ErrorCode;
import org.example.warehouseservice.repository.ItemImageRepository;
import org.example.warehouseservice.repository.ItemRepository;
import org.example.warehouseservice.service.ItemImageService;
import org.example.warehouseservice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemImageServiceImpl implements ItemImageService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemImageRepository itemImageRepository;
    @Autowired
    private S3Service s3Service;

    @Override
    @Transactional
    public List<String> uploadItemImages(Long itemId, List<MultipartFile> files) throws IOException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));
        List<String> imageUrls = s3Service.upload(files, "items/" + itemId);
        List<ItemImage> itemImages = new ArrayList<>();
        for (String url : imageUrls) {
            ItemImage image = new ItemImage();
            image.setImageUrl(url);
            image.setItem(item);
            itemImages.add(image);
        }
        itemImageRepository.saveAll(itemImages);
        return imageUrls;
    }

    @Override
    @Transactional
    public void deleteItemImage(Long imageId) {
        ItemImage image = itemImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));

        String imageUrl = image.getImageUrl();
        String objectKey = extractKeyFromUrl(imageUrl);
        s3Service.delete(objectKey);
        itemImageRepository.delete(image);
    }

    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new AppException(ErrorCode.S3_DELETE_FAILED);
        }
        int index = imageUrl.indexOf(".amazonaws.com/");
        if (index == -1) {
            throw new AppException(ErrorCode.S3_DELETE_FAILED);
        }
        return imageUrl.substring(index + ".amazonaws.com/".length());
    }
}
