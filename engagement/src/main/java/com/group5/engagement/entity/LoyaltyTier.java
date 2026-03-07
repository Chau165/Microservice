package com.group5.engagement.entity;

import com.group5.engagement.base.BaseEntity;
import com.group5.engagement.constants.TierName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loyalty_tier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyTier extends BaseEntity {

    @Column(name = "franchise_id", nullable = false)
    private Long franchiseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TierName name;

    @Column(name = "min_points")
    private Integer minPoints;

    @Column(name = "tier_multiplier")
    private Double tierMultiplier; // Hệ số nhân điểm (VD: 1.5x)

    @Column(columnDefinition = "TEXT")
    private String benefits;
}