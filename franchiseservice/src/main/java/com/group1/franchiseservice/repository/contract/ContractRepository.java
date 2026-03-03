package com.group1.franchiseservice.repository.contract;

import com.group1.franchiseservice.model.entity.contract.Contract;
import com.group1.franchiseservice.model.entity.contract.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    boolean existsByContractNumber(String contractNumber);

    boolean existsByFranchiseIdAndStatus(UUID franchiseId, ContractStatus status);
}
