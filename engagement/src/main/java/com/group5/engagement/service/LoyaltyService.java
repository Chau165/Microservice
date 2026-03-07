package com.group5.engagement.service;

import com.group5.engagement.constants.TierName;
import com.group5.engagement.dto.request.CreateLoyaltyTierRequest;
import com.group5.engagement.dto.request.LoyaltyRuleRequest;
import com.group5.engagement.dto.request.RedeemRequest;
import com.group5.engagement.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoyaltyService {

    CustomerEngagementResponse getCustomerEngagement(Long customerId, Long franchiseId);

    List<TransactionHistoryResponse> getTransactionHistory(Long customerId, Long franchiseId);

    Page<CustomerEngagementResponse> getAllCustomers(
            Long franchiseId,
            Long tierId,
            Pageable pageable
    );

    // ===== Loyalty Tier =====
    LoyaltyTierResponse createTier(CreateLoyaltyTierRequest request);

    @Transactional(readOnly = true)
    List<LoyaltyTierResponse> getAllTiers();

    LoyaltyTierResponse updateTier(Long franchiseId,
                                   TierName name,
                                   CreateLoyaltyTierRequest request);
    void deleteTier(Long franchiseId, TierName tierName);
    //======  LoyaltyRule =======
    LoyaltyRuleResponse createRule(Long franchiseId, LoyaltyRuleRequest request);
    List<LoyaltyRuleResponse> getAllRules();
    LoyaltyRuleResponse updateRule(Long franchiseId, String eventType, LoyaltyRuleRequest request);
    void deleteRule(Long franchiseId, String eventType);

    // ===== Redeem =====
    RedeemResponse redeem(RedeemRequest redeemRequest);
}