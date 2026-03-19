package com.group1.app.metadata.dto.contract.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RenewContractResponse(
        UUID contractId,
        LocalDate newEndDate,
        String status,
        LocalDateTime renewedAt,
        String renewedBy
) {}
