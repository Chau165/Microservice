package com.group1.app.metadata.event.franchise;

import java.util.UUID;

public record FranchiseCreatedEvent(
        UUID franchiseId,
        String franchiseCode
) {}

