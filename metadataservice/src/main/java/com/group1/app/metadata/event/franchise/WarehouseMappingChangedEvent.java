package com.group1.app.metadata.event.franchise;

import java.time.LocalDateTime;
import java.util.UUID;

public record WarehouseMappingChangedEvent(
        UUID franchiseId,
        String oldWarehouseId,
        String newWarehouseId,
        String changedBy,
        LocalDateTime timestamp
) {}
