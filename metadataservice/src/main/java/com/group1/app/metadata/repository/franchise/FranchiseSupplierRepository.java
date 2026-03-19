package com.group1.app.metadata.repository.franchise;

import com.group1.app.metadata.entity.franchise.FranchiseSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FranchiseSupplierRepository extends JpaRepository<FranchiseSupplier, UUID> {

    List<FranchiseSupplier> findByFranchiseId(UUID franchiseId);

    boolean existsByFranchiseIdAndSupplierId(UUID franchiseId, UUID supplierId);

}
