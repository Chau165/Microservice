package com.group1.app.metadata.entity.franchise;

import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "franchise_warehouse_mapping")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseWarehouseMapping extends BaseEntity {

    @Column(name = "warehouse_id", nullable = false)
    private String warehouseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FranchiseWarehouseMappingStatus status;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "unassigned_at")
    private Instant unassignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;
}
