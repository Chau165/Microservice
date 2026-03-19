package com.group1.app.metadata.event.franchise.franchisestaff;

import java.time.Instant;
import java.util.UUID;

public record StaffRemovedFromFranchiseEvent(
        UUID franchiseId,
        String staffId,
        Instant timestamp
) {}
