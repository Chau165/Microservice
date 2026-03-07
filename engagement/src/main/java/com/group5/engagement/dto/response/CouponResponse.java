package com.group5.engagement.dto.response;

import com.group5.engagement.entity.LoyaltyTier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
    private Long id;
    private Long promotionId;
    private String code;
    private String discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Double maxDiscount;
    private Integer usageLimit;
    private Integer userLimit;
    private Integer usedCount;
    private LoyaltyTier minTier;
    private Boolean isPublic;
}
