package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.contract.request.CreateContractRequest;
import com.group1.app.metadata.dto.contract.request.RenewContractRequest;
import com.group1.app.metadata.dto.contract.request.TerminateContractRequest;
import com.group1.app.metadata.dto.contract.response.*;
import com.group1.app.metadata.entity.contract.Contract;
import com.group1.app.metadata.entity.contract.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ContractService {

    CreateContractResponse create(CreateContractRequest request);

    RenewContractResponse renew(UUID id, RenewContractRequest request, String renewedBy);

    TerminateContractResponse terminate(
            UUID id,
            TerminateContractRequest request,
            String terminatedBy);

    ActivateContractResponse activate(UUID id, String activatedBy);

    ContractResponse getById(UUID id);

    Page<ContractListResponse> getAll(Pageable pageable);

    Page<ContractListResponse> searchContracts(
            UUID franchiseId, ContractStatus status,
            LocalDate startDateFrom, LocalDate startDateTo, Pageable pageable);

}
