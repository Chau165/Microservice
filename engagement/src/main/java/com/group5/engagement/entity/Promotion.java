package com.group5.engagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group5.engagement.base.BaseEntity;
import com.group5.engagement.constants.PromotionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion extends BaseEntity {

    @Column(name = "franchise_id")
    private Long franchiseId;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private PromotionStatus status = PromotionStatus.DRAFT;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "promotion")
    @JsonIgnore
    private List<Coupon> coupons;

    @Column(name = "is_active")
    private boolean isActive = true;

    private String type;
}