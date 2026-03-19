package com.group1.app.metadata.event.franchise;

import java.time.LocalDateTime;
import java.util.UUID;

public record MenuProfileAssignedEvent(

        UUID franchiseId,
        UUID menuProfileId,
        UUID previousMenuProfile,
        String assignedBy,
        LocalDateTime timestamp

) {
}
