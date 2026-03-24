package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateWarehouseMappingRequest(
        @NotNull(message = "Franchise ID must not be null")
        UUID franchiseId,
        
        @NotBlank(message = "Warehouse ID must not be empty")
        String warehouseId
) {}
