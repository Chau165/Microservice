package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateFranchiseRequest(
        @NotBlank(message = "Franchise name must not be empty")
        String franchiseName,

        @NotBlank(message = "Franchise code must not be empty")
        String franchiseCode,

        @NotBlank(message = "Address must not be empty")
        String address,

        @NotBlank(message = "Region must not be empty")
        String region,

        @NotBlank(message = "Timezone must not be empty")
        String timezone,

        UUID ownerId,

        String contactInfo
) {}

