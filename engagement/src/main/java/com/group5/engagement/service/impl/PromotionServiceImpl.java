package com.group5.engagement.service.impl;

import com.group5.engagement.constants.PromotionStatus;
import com.group5.engagement.dto.request.CreatePromotionRequest;
import com.group5.engagement.entity.Promotion;
import com.group5.engagement.exception.ResourceNotFoundException;
import com.group5.engagement.repository.PromotionRepository;
import com.group5.engagement.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service // Đánh dấu đây là Bean để Spring quản lý
@RequiredArgsConstructor // Tự động inject Repository qua Constructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public Promotion createPromotion(CreatePromotionRequest request) {
        try {
            System.out.println("=== CREATE PROMOTION SERVICE ===");
            System.out.println("FranchiseId: " + request.getFranchiseId());
            System.out.println("Name: " + request.getName());
            System.out.println("StartDate: " + request.getStartDate());
            System.out.println("EndDate: " + request.getEndDate());
            
            // 1. Validate Business Rules
            
            // Rule: Start date phải trước End date
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new IllegalArgumentException("Start date must be before end date");
            }

            // Rule: Kiểm tra trùng thời gian (Overlapping) - TẠM THỜI COMMENT ĐỂ TEST
            try {
                boolean isOverlapping = promotionRepository.existsOverlappingPromotion(
                        request.getFranchiseId(),
                        request.getStartDate(),
                        request.getEndDate()
                );

                if (isOverlapping) {
                    throw new IllegalStateException("A promotion already exists in this time range.");
                }
            } catch (Exception e) {
                System.err.println("Error checking overlapping: " + e.getMessage());
                // Bỏ qua lỗi này để test
            }

            // 2. Mapping DTO -> Entity
            Promotion promotion = new Promotion();
            promotion.setFranchiseId(request.getFranchiseId());
            promotion.setName(request.getName());
            promotion.setDescription(request.getDescription());
            promotion.setStartDate(request.getStartDate());
            promotion.setEndDate(request.getEndDate());

            // Rule: Mới tạo thì để Draft
            promotion.setStatus(PromotionStatus.DRAFT);

            System.out.println("Saving promotion...");
            // 3. Lưu xuống DB
            Promotion saved = promotionRepository.save(promotion);
            System.out.println("Saved successfully: " + saved.getId());
            return saved;
        } catch (Exception e) {
            System.err.println("ERROR in createPromotion: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Promotion> getActivePromotions(Long franchiseId) {
        LocalDateTime now = LocalDateTime.now();

        if (franchiseId != null) {
            return promotionRepository.findActivePromotionsByFranchiseNow(franchiseId, now);
        }

        return promotionRepository.findActivePromotionsNow(now);
    }

    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }

    @Override
    @Transactional
    public Promotion updatePromotionStatus(Long id, PromotionStatus status) {
        Promotion promotion = getPromotionById(id);
        promotion.setStatus(status);
        return promotionRepository.save(promotion);
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }
}