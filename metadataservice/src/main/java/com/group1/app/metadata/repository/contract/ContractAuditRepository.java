package com.group1.app.metadata.repository.contract;

import com.group1.app.metadata.entity.contract.ContractAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractAuditRepository extends JpaRepository<ContractAudit, UUID> {
}
