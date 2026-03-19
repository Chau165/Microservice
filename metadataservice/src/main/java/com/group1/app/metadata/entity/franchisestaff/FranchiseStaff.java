package com.group1.app.metadata.entity.franchisestaff;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import com.group1.app.metadata.entity.franchise.Franchise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "franchise_staff")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseStaff extends BaseEntity {

    @Column(name = "staff_id", nullable = false)
    private String staffId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FranchiseStaffStatus status;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "unassigned_at")
    private Instant unassignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    @JsonBackReference
    private Franchise franchise;
}
