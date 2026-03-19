package com.group1.app.metadata.event.contract;

import java.util.UUID;

public record ContractCreatedEvent(
        UUID contractId,
        String contractNumber
) {}
