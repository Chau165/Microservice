package com.group1.app.metadata.repository.franchise;

import com.group1.app.metadata.entity.franchise.OperationalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationalConfigRepository extends JpaRepository<OperationalConfig, UUID> {
    Optional<OperationalConfig> findByFranchiseId(UUID franchiseId);
}

