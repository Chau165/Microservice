package com.group1.app.metadata.entity.contract;

import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contract_audits")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContractAudit extends BaseEntity {

    @Column(nullable = false)
    private UUID contractId;

    @Enumerated(EnumType.STRING)
    private ContractStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ContractStatus newStatus;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String changedBy;
}
