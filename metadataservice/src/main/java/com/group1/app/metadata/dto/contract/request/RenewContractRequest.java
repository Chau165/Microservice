package com.group1.app.metadata.dto.contract.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RenewContractRequest(
        @NotNull(message = "New end date must not be null")
        LocalDate newEndDate
) {}
