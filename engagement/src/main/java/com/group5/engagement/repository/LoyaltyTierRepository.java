package com.group5.engagement.repository;

import com.group5.engagement.constants.TierName;
import com.group5.engagement.entity.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {

    // Tìm hạng thành viên có điểm yêu cầu <= điểm hiện tại của user (Lấy hạng cao nhất)
    // Câu Query này hơi nâng cao một chút, nó tìm hạng cao nhất mà user đạt được
    @Query("SELECT t FROM LoyaltyTier t WHERE t.franchiseId = :franchiseId AND t.minPoints <= :points ORDER BY t.minPoints DESC LIMIT 1")
    Optional<LoyaltyTier> findHighestTierByPoints(Long franchiseId, Integer points);

    boolean existsByFranchiseIdAndName(Long franchiseId, TierName name);
    List<LoyaltyTier> findByFranchiseId(Long franchiseId);
    Optional<LoyaltyTier> findByFranchiseIdAndName(Long franchiseId, TierName name);
    void deleteById(Long id);
}