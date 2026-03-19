package com.group1.app.metadata.event.contract;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ContractRenewedEvent(
        UUID contractId,
        UUID franchiseId,
        LocalDate newEndDate,
        String renewedBy,
        LocalDateTime timestamp
) {}
