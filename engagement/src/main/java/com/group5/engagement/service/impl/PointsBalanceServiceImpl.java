package com.group5.engagement.service.impl;

import com.group5.engagement.dto.response.PointsBalanceResponse;
import com.group5.engagement.entity.CustomerFranchise;
import com.group5.engagement.mapper.PointsBalanceMapper;
import com.group5.engagement.repository.CustomerFranchiseRepository;
import com.group5.engagement.service.PointsBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointsBalanceServiceImpl implements PointsBalanceService {
    @Autowired
    private CustomerFranchiseRepository customerFranchiseRepository;

    @Autowired
    private PointsBalanceMapper pointsBalanceMapper;

    @Override
    public PointsBalanceResponse getPointsBalance(Long customerId, Long franchiseId) {
        if (customerId == null || franchiseId == null) {
            return null;
        }

        CustomerFranchise customerFranchise = customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId)
                .orElse(null);

        if (customerFranchise == null) {
            return null;
        }

        return pointsBalanceMapper.toDTO(customerFranchise);
    }

}
