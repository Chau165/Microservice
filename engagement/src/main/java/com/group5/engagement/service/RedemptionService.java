package com.group5.engagement.service;

import com.group5.engagement.dto.response.RedemptionQRResponse;

public interface RedemptionService {
    RedemptionQRResponse confirmRedeem(Long rewardId) ;
}
