package com.group1.app.metadata.entity.franchise;

import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "franchise_suppliers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"franchise_id", "supplier_id"})
)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseSupplier extends BaseEntity {

    @Column(name = "franchise_id", nullable = false)
    UUID franchiseId;

    @Column(name = "supplier_id", nullable = false)
    UUID supplierId;

    @Column(name = "approved_by")
    String approvedBy;

    @Column(name = "approved_at")
    LocalDateTime approvedAt;
}
