package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateFranchiseRequest(
        @NotBlank(message = "Franchise name must not be empty")
        String franchiseName,

        @NotBlank(message = "Address must not be empty")
        String address,

        @NotBlank(message = "Region must not be empty")
        String region,

        String contactInfo,

        @NotBlank(message = "Timezone must not be empty")
        String timezone
) {}

