package service.CSFC.CSFC_auth_service.service.imp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.CSFC.CSFC_auth_service.common.exception.coupon.CouponNotFoundException;
import service.CSFC.CSFC_auth_service.common.exception.coupon.InvalidCouponException;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.dto.request.ApplyCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.GenerateCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApplyCouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.GenerateCouponResponse;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;
import service.CSFC.CSFC_auth_service.repository.CouponRepository;
import service.CSFC.CSFC_auth_service.repository.LoyaltyTierRepository;
import service.CSFC.CSFC_auth_service.repository.PromotionRepository;
import service.CSFC.CSFC_auth_service.service.CouponCodeGeneratorService;
import service.CSFC.CSFC_auth_service.service.CouponService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private CouponCodeGeneratorService codeGeneratorService;

    @Autowired
    private LoyaltyTierRepository loyaltyTierRepository;

    @Override
    @Transactional
    public GenerateCouponResponse generateCoupons(GenerateCouponRequest request) {
        long startTime = System.currentTimeMillis();

        // Validate promotion exists
        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + request.getPromotionId()));

        // Get existing codes with same prefix to avoid duplicates
        List<String> existingCodesList = couponRepository.findCodesStartingWith(request.getCodePrefix());
        Set<String> existingCodes = new HashSet<>(existingCodesList);

        // Generate unique codes
        boolean numericOnly = request.getUseNumericOnly() != null && request.getUseNumericOnly();
        Set<String> generatedCodes = codeGeneratorService.generateUniqueCodes(
                request.getQuantity(),
                request.getCodeLength(),
                request.getCodePrefix(),
                numericOnly,
                existingCodes);

        // Create coupon entities
        List<Coupon> coupons = new ArrayList<>();
        for (String code : generatedCodes) {
            Coupon coupon = new Coupon();
            coupon.setCode(code);
            coupon.setPromotion(promotion);
            coupon.setUsageLimit(request.getUsageLimit());
            coupon.setUserLimit(request.getUserLimit() != null ? request.getUserLimit() : 1);
            coupon.setUsedCount(0);
            coupon.setDiscountType(request.getDiscountType());
            coupon.setDiscountValue(request.getDiscountValue());
            coupon.setMinOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : 0.0);
            coupon.setMaxDiscount(request.getMaxDiscount());
            coupon.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);

            if (request.getMinTierId() != null) {
                LoyaltyTier tier = loyaltyTierRepository.findById(request.getMinTierId())
                        .orElseThrow(() -> new RuntimeException(
                                "Loyalty tier not found with id: " + request.getMinTierId()));
                coupon.setMinTier(tier);
            }

            coupons.add(coupon);
        }

        // Bulk insert using saveAll (batch processing)
        List<Coupon> savedCoupons = couponRepository.saveAll(coupons);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Build response
        GenerateCouponResponse response = new GenerateCouponResponse();
        response.setPromotionId(request.getPromotionId());
        response.setTotalGenerated(savedCoupons.size());
        response.setSuccessCount(savedCoupons.size());
        response.setFailedCount(0);
        response.setGeneratedCodes(new ArrayList<>(generatedCodes));
        response.setExecutionTimeMs(executionTime);
        response.setGeneratedAt(LocalDateTime.now());
        response.setMessage("Successfully generated " + savedCoupons.size() + " coupon codes");

        // Add statistics
        GenerateCouponResponse.GenerationStats stats = new GenerateCouponResponse.GenerationStats();
        stats.setTotalRequested(request.getQuantity());
        stats.setTotalGenerated(savedCoupons.size());
        stats.setDuplicatesSkipped(request.getQuantity() - savedCoupons.size());
        stats.setExecutionTimeMs(executionTime);
        stats.setCodesPerSecond(executionTime > 0 ? (savedCoupons.size() * 1000.0) / executionTime : 0);
        response.setStats(stats);

        return response;
    }

    @Override
    public boolean validateCoupon(String code) {
        return couponRepository.existsByCode(code);
    }

    @Override
    public GenerateCouponResponse.GenerationStats getGenerationStats(Long promotionId) {
        // Đếm tổng số coupon trong database cho promotion này
        Long totalCoupons = couponRepository.countByPromotionId(promotionId);

        GenerateCouponResponse.GenerationStats stats = new GenerateCouponResponse.GenerationStats();
        // totalGenerated = tổng coupon hiện có trong DB
        stats.setTotalGenerated(totalCoupons.intValue());

        // Các field này chỉ có giá trị trong response của generateCoupons()
        // Endpoint /stats chỉ trả về snapshot hiện tại của database
        stats.setTotalRequested(totalCoupons.intValue()); // Số hiện có
        stats.setDuplicatesSkipped(0);
        stats.setExecutionTimeMs(null);
        stats.setCodesPerSecond(null);

        return stats;
    }

    @Override
    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    @Override
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    @Override
    public CouponResponse updateCoupon(Long id, Coupon coupon) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        existing.setDiscountType(coupon.getDiscountType());
        existing.setDiscountValue(coupon.getDiscountValue());
        existing.setMinOrderValue(coupon.getMinOrderValue());
        existing.setMaxDiscount(coupon.getMaxDiscount());
        existing.setUsageLimit(coupon.getUsageLimit());
        existing.setUserLimit(coupon.getUserLimit());
        existing.setMinTier(coupon.getMinTier());
        existing.setIsPublic(coupon.getIsPublic());

        Coupon updated = couponRepository.save(existing);

        return mapToResponse(updated);
    }

    @Override
    public CouponResponse createCoupon(CouponRequest request) {

        Coupon coupon = new Coupon();

        if (request.getPromotionId() == null) {
            throw new IllegalArgumentException("Promotion ID must not be null");
        }

        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        coupon.setPromotion(promotion);
        coupon.setCode(request.getCode());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setMaxDiscount(request.getMaxDiscount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUserLimit(request.getUserLimit());
        coupon.setIsPublic(request.getIsPublic());

        if (request.getMinTierId() != null) {
            LoyaltyTier tier = loyaltyTierRepository.findById(request.getMinTierId())
                    .orElseThrow(() -> new RuntimeException("Tier not found"));
            coupon.setMinTier(tier);
        }
        return mapToResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public ApplyCouponResponse applyCoupon(ApplyCouponRequest req) {
        if (req.getCouponCode() == null || req.getCouponCode().isEmpty()) {
            throw new InvalidCouponException("Mã giá giảm giá không hợp lệ");
        }

        Coupon coupon = couponRepository
                .findByCode(req.getCouponCode())
                .orElseThrow(CouponNotFoundException::new);

        validateCoupon(coupon, req);

        double discount = calculateDiscount(coupon, req.getOrderAmount());
        double finalAmount = req.getOrderAmount() - discount;

        int usedCount = coupon.getUsedCount() == null ? 0 : coupon.getUsedCount();
        coupon.setUsedCount(usedCount + 1);
        couponRepository.save(coupon);

        return new ApplyCouponResponse(
                req.getOrderAmount(),
                discount,
                finalAmount);
    }

    private double calculateDiscount(Coupon coupon, double amount) {

        double discount = 0;

        if ("FIXED_AMOUNT".equals(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        }

        if ("PERCENTAGE".equals(coupon.getDiscountType())) {

            discount = amount * coupon.getDiscountValue() / 100;

            if (coupon.getMaxDiscount() != null && coupon.getMaxDiscount() > 0) {
                discount = Math.min(discount, coupon.getMaxDiscount());
            }
        }

        return Math.min(discount, amount);
    }

    private void validateCoupon(Coupon coupon,
            ApplyCouponRequest req) {
        LocalDateTime now = LocalDateTime.now();
        Promotion promotion = coupon.getPromotion();

        Integer usageLimit = coupon.getUsageLimit();
        int usedCount = coupon.getUsedCount() == null ? 0 : coupon.getUsedCount();
        if (usageLimit != null && (usageLimit == 0 || usedCount >= usageLimit)) {
            throw new InvalidCouponException("Mã giảm giá hết lượt dùng");
        }

        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
            throw new InvalidCouponException("Mã giảm giá đã hết hạn");
        }

        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
            throw new InvalidCouponException("Mã giảm giá chưa bắt đầu");
        }

        double minOrderValue = coupon.getMinOrderValue() == null ? 0.0 : coupon.getMinOrderValue();
        if (req.getOrderAmount() < minOrderValue) {
            throw new InvalidCouponException("Tổng số tiền đơn hàng không đủ để áp dụng mã giảm giá");
        }

        if (promotion.getStatus() != PromotionStatus.ACTIVE) {
            throw new InvalidCouponException("Hiện không có chương trình khuyến mãi này!");
        }

        if (!Boolean.TRUE.equals(coupon.getIsPublic())) {
            throw new InvalidCouponException("Bạn không có quyền sử dụng mã giảm giá này!");
        }
    }

    private CouponResponse mapToResponse(Coupon coupon) {

        CouponResponse response = new CouponResponse();

        response.setId(coupon.getId());
        response.setPromotionId(coupon.getPromotion().getId());
        response.setCode(coupon.getCode());
        response.setDiscountType(coupon.getDiscountType());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setMinOrderValue(coupon.getMinOrderValue());
        response.setMaxDiscount(coupon.getMaxDiscount());
        response.setUsageLimit(coupon.getUsageLimit());
        response.setUserLimit(coupon.getUserLimit());
        response.setUsedCount(coupon.getUsedCount());
        response.setMinTier(coupon.getMinTier());
        response.setIsPublic(coupon.getIsPublic());

        return response;
    }

}
