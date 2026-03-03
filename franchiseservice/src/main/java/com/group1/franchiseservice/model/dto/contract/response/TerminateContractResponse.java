package com.group1.franchiseservice.model.dto.contract.response;

import java.util.UUID;

public record TerminateContractResponse(
        UUID contractId,
        String status
) {}
