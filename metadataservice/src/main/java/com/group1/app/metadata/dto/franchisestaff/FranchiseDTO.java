package com.group1.app.metadata.dto.franchisestaff;

import com.group1.app.metadata.entity.franchise.Franchise;

import java.util.UUID;

public record FranchiseDTO(
        UUID franchiseId,
        String franchiseName,
        String franchiseCode,
        String address,
        String region,
        String timezone,
        String status
) {
    public static FranchiseDTO from(Franchise f) {
        return new FranchiseDTO(
                f.getId(),
                f.getFranchiseName(),
                f.getFranchiseCode(),
                f.getAddress(),
                f.getRegion(),
                f.getTimezone(),
                f.getStatus().name()
        );
    }
}
