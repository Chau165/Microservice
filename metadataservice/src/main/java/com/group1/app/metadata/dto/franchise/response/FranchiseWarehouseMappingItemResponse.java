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
) {
    // Backward-compatible JavaBean getters for existing tests/usages.
    public UUID getId() { return id; }
    public String getWarehouseId() { return warehouseId; }
    public String getStatus() { return status; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getUnassignedAt() { return unassignedAt; }
    public UUID getFranchiseId() { return franchiseId; }
}
