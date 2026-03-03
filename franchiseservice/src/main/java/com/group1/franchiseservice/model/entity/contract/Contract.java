package com.group1.franchiseservice.model.entity.contract;

import com.group1.franchiseservice.infrastructure.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contracts",
        uniqueConstraints = @UniqueConstraint(columnNames = "contractNumber"))
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String contractNumber;

    @Column(nullable = false)
    private UUID franchiseId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double royaltyRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    @Column(nullable = false)
    private boolean autoOrderEnabled;
}