package com.group1.app.metadata.event.franchise;

import java.time.LocalDateTime;
import java.util.UUID;

public record FranchiseSuspendedEvent(

        UUID franchiseId,
        String reason,
        String suspendedBy,
        LocalDateTime timestamp

) {
}
