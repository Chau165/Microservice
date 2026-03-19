package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateWarehouseMappingRequest(
        @NotBlank(message = "Warehouse ID must not be empty")
        String warehouseId
) {}
