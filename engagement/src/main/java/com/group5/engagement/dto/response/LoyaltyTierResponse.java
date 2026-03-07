package com.group5.engagement.dto.response;

import com.group5.engagement.constants.TierName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoyaltyTierResponse {

    private Long id;
    private Long franchiseId;
    private TierName name;
    private Integer minPoints;
    private Double tierMultiplier;
    private String benefits;
}