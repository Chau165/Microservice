package com.group1.app.metadata.repository.franchise;

import com.group1.app.metadata.entity.franchise.FranchiseAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FranchiseAuditRepository extends JpaRepository<FranchiseAudit, UUID> {
}

