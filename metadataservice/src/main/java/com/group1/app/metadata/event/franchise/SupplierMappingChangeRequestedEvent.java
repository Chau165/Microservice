package com.group1.app.metadata.event.franchise;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SupplierMappingChangeRequestedEvent {
    private UUID requestId;

    private UUID franchiseId;

    private UUID supplierId;

    private String reason;
}
