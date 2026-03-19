package com.group1.app.metadata.event.franchise;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SupplierListApprovedEvent(
        UUID franchiseId,
        List<UUID> supplierIds,
        String approvedBy,
        LocalDateTime timestamp
) {
}
