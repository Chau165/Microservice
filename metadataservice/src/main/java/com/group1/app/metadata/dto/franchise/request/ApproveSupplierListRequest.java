package com.group1.app.metadata.dto.franchise.request;

import java.util.List;
import java.util.UUID;

public record ApproveSupplierListRequest(
        UUID franchiseId,
        List<UUID> supplierIds
) {
}
