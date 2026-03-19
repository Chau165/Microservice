package com.group1.app.metadata.event.franchise;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FranchiseOwnershipTransferredEvent(

        UUID franchiseId,
        UUID oldOwnerId,
        UUID newOwnerId,
        String role,
        LocalDate effectiveDate,
        String changedBy,
        LocalDateTime timestamp

) {
}
