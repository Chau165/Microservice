package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotBlank;

public record SuspendFranchiseRequest(

        @NotBlank(message = "Suspension reason is required")
        String reason

) {
}
