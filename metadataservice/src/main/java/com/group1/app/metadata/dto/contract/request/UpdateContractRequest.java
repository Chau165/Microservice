package com.group1.app.metadata.dto.contract.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateContractRequest(
        @NotBlank(message = "Contract number must not be empty")
        @Pattern(regexp = "^\\S(.*\\S)?$", message = "Contract number must not start or end with whitespace")
        @Size(max = 255, message = "Contract number must be at most 255 characters")
        String contractNumber,

        @NotNull(message = "Start date must not be null")
        LocalDate startDate,

        @NotNull(message = "End date must not be null")
        LocalDate endDate,

        @Positive(message = "Royalty rate must be greater than 0")
        @DecimalMax(value = "100.0", message = "Must be <= 100")
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal royaltyRate
) {}