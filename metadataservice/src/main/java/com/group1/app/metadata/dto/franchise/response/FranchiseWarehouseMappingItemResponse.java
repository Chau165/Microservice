package com.group1.app.metadata.dto.franchise.response;

import java.time.Instant;
import java.util.UUID;

public record FranchiseWarehouseMappingItemResponse(
        UUID id,
        String warehouseId,
        String status,
        Instant assignedAt,
        Instant unassignedAt,
        UUID franchiseId
) {}
