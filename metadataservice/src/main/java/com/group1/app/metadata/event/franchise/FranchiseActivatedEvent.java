package com.group1.app.metadata.event.franchise;

import java.time.LocalDateTime;
import java.util.UUID;

public record FranchiseActivatedEvent(
        UUID franchiseId,
        String activatedBy,
        LocalDateTime timestamp
) {}

