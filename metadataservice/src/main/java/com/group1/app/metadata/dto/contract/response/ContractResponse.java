package com.group1.app.metadata.dto.contract.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ContractResponse(
        UUID contractId,
        String contractNumber,
        UUID franchiseId,
        String franchiseCode,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime activatedAt,
        String activatedBy,
        LocalDateTime renewedAt,
        String renewedBy,
        LocalDateTime terminatedAt,
        String terminatedBy,

        BigDecimal royaltyRate,
        boolean autoOrderEnabled,
        String terminationReason
) {}