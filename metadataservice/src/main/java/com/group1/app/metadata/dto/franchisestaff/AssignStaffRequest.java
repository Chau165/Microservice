package com.group1.app.metadata.dto.franchisestaff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignStaffRequest(

        @NotNull
        UUID franchiseId,

        @NotBlank
        String staffId

) {}