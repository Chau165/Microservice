package com.group1.franchiseservice.model.dto.contract.response;

import java.time.LocalDate;
import java.util.UUID;

public record RenewContractResponse(
        UUID contractId,
        LocalDate newEndDate
) {}
