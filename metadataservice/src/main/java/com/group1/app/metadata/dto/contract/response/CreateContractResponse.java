package com.group1.app.metadata.dto.contract.response;


import java.util.UUID;

public record CreateContractResponse(
        UUID contractId,
        String contractNumber,
        String status
) {}
