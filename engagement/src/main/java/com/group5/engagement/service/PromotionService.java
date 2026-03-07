package com.group5.engagement.service;

import com.group5.engagement.constants.PromotionStatus;
import com.group5.engagement.dto.request.CreatePromotionRequest;
import com.group5.engagement.entity.Promotion;

import java.util.List;

public interface PromotionService {
    // Chỉ khai báo hàm, không viết code xử lý
    Promotion createPromotion(CreatePromotionRequest request);

    List<Promotion> getActivePromotions(Long franchiseId);

    Promotion getPromotionById(Long id);

    Promotion updatePromotionStatus(Long id, PromotionStatus status);

    List<Promotion> getAllPromotions();
}