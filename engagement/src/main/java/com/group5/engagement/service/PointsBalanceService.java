package com.group5.engagement.service;

import com.group5.engagement.dto.response.PointsBalanceResponse;


public interface PointsBalanceService {
    PointsBalanceResponse getPointsBalance(Long customerId, Long franchiseId);
}
