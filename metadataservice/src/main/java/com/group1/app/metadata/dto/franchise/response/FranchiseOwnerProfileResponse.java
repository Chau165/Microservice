package com.group1.app.metadata.dto.franchise.response;

import java.util.UUID;

public record FranchiseOwnerProfileResponse(
        UUID ownerId,
        String userId,
        String staffId,
        String name,
        String email,
        String phone,
        String branchId,
        String staffCode,
        String status
) {}
