package com.group1.app.metadata.event.contract;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContractActivatedEvent(
        UUID contractId,
        UUID franchiseId,
        String activatedBy,
        LocalDateTime timestamp
) {}
