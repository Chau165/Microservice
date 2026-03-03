package com.group1.franchiseservice.model.dto.contract.request;

import jakarta.validation.constraints.NotBlank;

public record TerminateContractRequest(
        @NotBlank(message = "Termination reason must not be empty")
        String terminationReason
) {}
