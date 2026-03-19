package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ChangeFranchiseOwnerRequest(

        @NotNull
        UUID newOwnerId,

        @NotNull
        String role,

        @NotNull
        LocalDate effectiveDate
) {
}
