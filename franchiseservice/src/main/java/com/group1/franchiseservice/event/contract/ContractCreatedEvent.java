package com.group1.franchiseservice.event.contract;

import java.util.UUID;

public record ContractCreatedEvent(
        UUID contractId,
        String contractNumber
) {}
