package com.group5.engagement.dto.request;

import lombok.Data;

@Data
public class ApplyCouponRequest {
    private long customerId;
    private String couponCode;
    private Double orderAmount;
}
