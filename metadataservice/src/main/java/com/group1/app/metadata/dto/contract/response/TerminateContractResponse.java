package com.group1.app.metadata.dto.contract.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TerminateContractResponse(
        UUID contractId,
        String status,
        LocalDateTime terminatedAt,
        String terminatedBy,
        String terminationReason
) {}
