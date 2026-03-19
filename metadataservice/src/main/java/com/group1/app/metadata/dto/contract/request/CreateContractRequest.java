package com.group1.app.metadata.dto.contract.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateContractRequest(
        @NotBlank(message = "Contract number must not be empty")
        String contractNumber,

        @NotNull(message = "Franchise ID must not be null")
        UUID franchiseId,

        @NotNull(message = "Start date must not be null")
        LocalDate startDate,

        @NotNull(message = "End date must not be null")
        LocalDate endDate,

        @Positive(message = "Royalty rate must be greater than 0")
        BigDecimal royaltyRate
) {}
