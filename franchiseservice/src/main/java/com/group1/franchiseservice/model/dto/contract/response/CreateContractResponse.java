package com.group1.franchiseservice.model.dto.contract.response;


import java.util.UUID;

public record CreateContractResponse(
        UUID contractId,
        String contractNumber,
        String status
) {}
