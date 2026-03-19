package com.group1.app.metadata.entity.brand;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    private UUID id;

    @Column(name = "brand_code", nullable = false)
    private String brandCode;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Column(name = "max_open_minutes_per_day")
    private Integer maxOpenMinutesPerDay;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
