package com.group1.app.metadata.repository.franchise;

import com.group1.app.metadata.entity.franchise.FranchiseWarehouseMapping;
import com.group1.app.metadata.entity.franchise.FranchiseWarehouseMappingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FranchiseWarehouseMappingRepository extends JpaRepository<FranchiseWarehouseMapping, UUID> {

    List<FranchiseWarehouseMapping> findAllByFranchise_Id(UUID franchiseId);

    Optional<FranchiseWarehouseMapping> findByFranchise_IdAndStatus(
            UUID franchiseId,
            FranchiseWarehouseMappingStatus status
    );
}
