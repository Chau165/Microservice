package com.group1.franchiseservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @Schema(defaultValue = "CUSTOMER", example = "CUSTOMER")
        @NotBlank String role
) {}