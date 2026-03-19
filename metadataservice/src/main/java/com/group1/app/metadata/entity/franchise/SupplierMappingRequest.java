package com.group1.app.metadata.entity.franchise;

import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "supplier_mapping_requests")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierMappingRequest extends BaseEntity {

    @Column(nullable = false)
    private UUID franchiseId;

    @Column(nullable = false)
    private UUID supplierId;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierMappingRequestStatus status;

    private String comment;
}