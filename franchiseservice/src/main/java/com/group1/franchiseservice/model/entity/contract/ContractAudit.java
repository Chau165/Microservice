package com.group1.franchiseservice.model.entity.contract;

import com.group1.franchiseservice.infrastructure.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String changedBy;
}