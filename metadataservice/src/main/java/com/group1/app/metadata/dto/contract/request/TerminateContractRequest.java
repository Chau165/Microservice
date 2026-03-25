package com.group1.app.metadata.dto.contract.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TerminateContractRequest(
        @NotBlank(message = "Termination reason must not be empty")
        @Pattern(regexp = "^\\S(.*\\S)?$", message = "Termination reason must not start or end with whitespace")
        @Size(max = 255, message = "Termination reason must be at most 255 characters")
        String terminationReason
) {}
