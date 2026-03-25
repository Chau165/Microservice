package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateFranchiseRequest(
        @NotBlank(message = "Franchise name must not be empty")
        @Pattern(regexp = "^\\S(.*\\S)?$", message = "Franchise name must not start or end with whitespace")
        @Size(max = 255, message = "Franchise name must be at most 255 characters")
        String franchiseName,

        @NotBlank(message = "Franchise code must not be empty")
        @Pattern(regexp = "^\\S(.*\\S)?$", message = "Franchise code must not start or end with whitespace")
        @Size(max = 255, message = "Franchise code must be at most 255 characters")
        String franchiseCode,

        @NotBlank(message = "Address must not be empty")
        @Pattern(regexp = "^\\S(.*\\S)?$", message = "Address must not start or end with whitespace")
        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,

        @NotBlank(message = "Region must not be empty")
        @Pattern(regexp = "^\\S(.*\\S)?$", message = "Region must not start or end with whitespace")
        @Size(max = 255, message = "Region must be at most 255 characters")
        String region,

        @NotBlank(message = "Timezone must not be empty")
        @Pattern(regexp = "^\\S(.*\\S)?$", message = "Timezone must not start or end with whitespace")
        @Size(max = 255, message = "Timezone must be at most 255 characters")
        String timezone,

        UUID ownerId,

        String contactInfo
) {}

