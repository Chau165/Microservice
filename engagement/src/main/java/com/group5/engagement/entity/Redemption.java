package com.group5.engagement.entity;

import com.group5.engagement.base.BaseEntity;
import com.group5.engagement.constants.RedemptionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "redemption")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Redemption extends BaseEntity {

    // Quan trọng: Link với CustomerFranchise (User tại cửa hàng đó)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_transaction_id")
    private PointTransaction pointTransaction;

    // Link với phần thưởng (nếu đổi quà hiện vật)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id")
    private Reward reward;

    // Link với khuyến mãi (nếu đổi voucher/promotion)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(name = "points_used", nullable = false)
    private Integer pointsUsed; // Số điểm đã trừ lúc đổi

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RedemptionStatus status; // PENDING, COMPLETED...

    @Column(name = "redemption_code")
    private String redemptionCode; // Mã đổi quà (để đưa thu ngân scan)

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt; // Thời điểm đổi thực tế
}