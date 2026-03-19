package com.group1.app.metadata.entity.contract;

import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contracts",
        uniqueConstraints = @UniqueConstraint(columnNames = "contractNumber"),
        indexes = {
                @Index(name = "idx_contract_franchise_status", columnList = "franchise_id,status"),
                @Index(name = "idx_contract_overlap", columnList = "franchise_id,status,start_date,end_date")
        })
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String contractNumber;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal royaltyRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    @Column(nullable = false)
    private boolean autoOrderEnabled;

    private LocalDateTime activatedAt;
    private String activatedBy;

    private LocalDateTime renewedAt;
    private String renewedBy;

    private LocalDateTime terminatedAt;
    private String terminatedBy;

    @Column(length = 500)
    private String terminationReason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    public boolean isExpired() {
        return status == ContractStatus.ACTIVE
                && endDate != null
                && endDate.isBefore(LocalDate.now());
    }
    public String getEffectiveStatus() {
        return isExpired() ? "EXPIRED" : status.name();
    }

}
