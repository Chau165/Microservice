package com.group1.app.metadata.event.franchise;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FranchiseOwnerAssignedEvent(

        UUID franchiseId,
        UUID ownerId,
        String role,
        LocalDate startDate,
        String assignedBy,
        LocalDateTime timestamp
) {
}
