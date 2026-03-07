package com.group5.engagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class RedeemResponse {
    private String redemptionCode;
    private Integer pointUsed;
    private Integer currentPoints;
}
