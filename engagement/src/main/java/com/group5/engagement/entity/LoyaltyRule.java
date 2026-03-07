package com.group5.engagement.entity;

import com.group5.engagement.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyRule extends BaseEntity {

    @Column(name = "franchise_id")
    private Long franchiseId;

    private String name; // VD: Tích điểm sinh nhật

    @Column(name = "event_type")
    private String eventType; // ORDER, REVIEW, REFERRAL

    @Column(name = "point_multiplier")
    private Double pointMultiplier;

    @Column(name = "fixed_points")
    private Integer fixedPoints;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    // ... các trường start_date, end_date (dùng LocalDateTime)
}