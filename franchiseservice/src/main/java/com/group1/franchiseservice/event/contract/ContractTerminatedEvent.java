package com.group1.franchiseservice.event.contract;

import java.util.UUID;

public record ContractTerminatedEvent(
        UUID contractId,
        String reason
) {}
