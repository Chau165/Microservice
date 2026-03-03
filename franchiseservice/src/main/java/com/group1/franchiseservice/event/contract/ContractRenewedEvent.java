package com.group1.franchiseservice.event.contract;

import java.time.LocalDate;
import java.util.UUID;

public record ContractRenewedEvent(
        UUID contractId,
        LocalDate newEndDate
) {}
