package com.group5.engagement.dto.request;

import com.group5.engagement.constants.TierName;
import lombok.Data;

@Data
public class CreateLoyaltyTierRequest {
    private Long franchiseId;
    private TierName name;
    private Double tierMultiplier;
    private String benefits;
}
