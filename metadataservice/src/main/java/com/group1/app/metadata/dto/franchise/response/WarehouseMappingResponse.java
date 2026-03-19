package com.group1.app.metadata.dto.franchise.response;

import java.time.Instant;
import java.util.UUID;

public record WarehouseMappingResponse(
        UUID franchiseId,
        String warehouseId,
        String status,
        Instant assignedAt
) {}
