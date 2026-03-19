package com.group1.app.metadata.dto.franchise.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignMenuProfileRequest(

        @NotNull
        UUID menuProfileId

) {
}