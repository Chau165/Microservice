package com.group5.engagement.entity;

import com.group5.engagement.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward extends BaseEntity {

    @Column(name = "franchise_id", nullable = false)
    private Long franchiseId; // Phần thưởng thuộc về chuỗi nào

    @Column(nullable = false)
    private String name; // Tên phần thưởng

    @Column(name = "required_points", nullable = false)
    private Integer requiredPoints; // Số điểm cần để đổi

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chi tiết

    @Column(name = "is_active")
    private Boolean isActive = true; // Còn áp dụng hay không
}