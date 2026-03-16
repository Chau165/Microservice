package service.CSFC.CSFC_auth_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;
import service.CSFC.CSFC_auth_service.model.constants.TierName;

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

    @Column(name = "total_earned_point")
    private Integer totalEarnedPoint;

    @Column(columnDefinition = "TEXT")
    private String benefits;

}