package com.group5.engagement.repository;

import com.group5.engagement.entity.LoyaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoyaltyRuleRepository extends JpaRepository<LoyaltyRule, Long> {
    List<LoyaltyRule> findByFranchiseId(Long franchiseId);
    Optional<LoyaltyRule> findByFranchiseIdAndEventType(Long franchiseId, String eventType);
    boolean existsByFranchiseIdAndEventTypeAndIsActive(Long franchiseId, String eventType, Boolean isActive);

}