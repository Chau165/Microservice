package com.group1.franchiseservice.service.contract;

import com.group1.franchiseservice.model.dto.contract.request.CreateContractRequest;
import com.group1.franchiseservice.model.dto.contract.request.RenewContractRequest;
import com.group1.franchiseservice.model.dto.contract.request.TerminateContractRequest;
import com.group1.franchiseservice.model.dto.contract.response.CreateContractResponse;
import com.group1.franchiseservice.model.dto.contract.response.RenewContractResponse;
import com.group1.franchiseservice.model.dto.contract.response.TerminateContractResponse;
import com.group1.franchiseservice.model.entity.contract.Contract;

import java.util.List;
import java.util.UUID;

public interface ContractService {
    public CreateContractResponse create(CreateContractRequest request);
    public RenewContractResponse renew(UUID id, RenewContractRequest request);
    public TerminateContractResponse terminate(
            UUID id,
            TerminateContractRequest request,
            String changedBy);

    Contract getById(UUID id);

    List<Contract> getAll();
}
