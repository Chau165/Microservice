package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record AssignFranchiseOwnerRequest(

        @NotNull
        UUID ownerId,

        @NotNull
        String role,

        @NotNull
        LocalDate effectiveDate
) {
}
