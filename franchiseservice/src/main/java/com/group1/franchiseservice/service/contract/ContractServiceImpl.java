package com.group1.franchiseservice.service.contract;

import com.group1.franchiseservice.common.exception.ApiException;
import com.group1.franchiseservice.common.exception.ErrorCode;
import com.group1.franchiseservice.event.contract.ContractCreatedEvent;
import com.group1.franchiseservice.event.contract.ContractRenewedEvent;
import com.group1.franchiseservice.event.contract.ContractTerminatedEvent;
import com.group1.franchiseservice.mapper.contract.ContractMapper;
import com.group1.franchiseservice.model.dto.contract.request.CreateContractRequest;
import com.group1.franchiseservice.model.dto.contract.request.RenewContractRequest;
import com.group1.franchiseservice.model.dto.contract.request.TerminateContractRequest;
import com.group1.franchiseservice.model.dto.contract.response.CreateContractResponse;
import com.group1.franchiseservice.model.dto.contract.response.RenewContractResponse;
import com.group1.franchiseservice.model.dto.contract.response.TerminateContractResponse;
import com.group1.franchiseservice.model.entity.contract.Contract;
import com.group1.franchiseservice.model.entity.contract.ContractAudit;
import com.group1.franchiseservice.model.entity.contract.ContractStatus;
import com.group1.franchiseservice.repository.contract.ContractAuditRepository;
import com.group1.franchiseservice.repository.contract.ContractRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ContractServiceImpl implements ContractService {

    ContractRepository contractRepository;
    ContractAuditRepository auditRepository;
    ApplicationEventPublisher eventPublisher;
    ContractMapper contractMapper;

    @Override
    public CreateContractResponse create(CreateContractRequest request) {

        if (!request.startDate().isBefore(request.endDate())) {
            throw new ApiException(ErrorCode.INVALID_DATE_RANGE);
        }

        if (contractRepository.existsByContractNumber(request.contractNumber())) {
            throw new ApiException(ErrorCode.CONTRACT_ALREADY_EXISTS);
        }

        if (contractRepository.existsByFranchiseIdAndStatus(
                request.franchiseId(),
                ContractStatus.ACTIVE)) {
            throw new ApiException(ErrorCode.ACTIVE_CONTRACT_OVERLAP);
        }

        Contract contract = contractMapper.toEntity(request);
        contract = contractRepository.save(contract);

        eventPublisher.publishEvent(
                new ContractCreatedEvent(contract.getId(), contract.getContractNumber())
        );

        return contractMapper.toCreateResponse(contract);
    }

    @Override
    public RenewContractResponse renew(UUID id, RenewContractRequest request) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new ApiException(ErrorCode.CONTRACT_NOT_ACTIVE);
        }

        if (!request.newEndDate().isAfter(contract.getEndDate())) {
            throw new ApiException(ErrorCode.INVALID_NEW_END_DATE);
        }

        contract.setEndDate(request.newEndDate());

        eventPublisher.publishEvent(
                new ContractRenewedEvent(contract.getId(), contract.getEndDate())
        );

        return contractMapper.toRenewResponse(contract);
    }

    @Override
    public TerminateContractResponse terminate(
            UUID id,
            TerminateContractRequest request,
            String changedBy) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() == ContractStatus.TERMINATED) {
            throw new ApiException(ErrorCode.CONTRACT_ALREADY_TERMINATED);
        }

        contract.setStatus(ContractStatus.TERMINATED);
        contract.setAutoOrderEnabled(false);

        auditRepository.save(
                ContractAudit.builder()
                        .contractId(contract.getId())
                        .reason(request.terminationReason())
                        .timestamp(LocalDateTime.now())
                        .changedBy(changedBy)
                        .build()
        );

        eventPublisher.publishEvent(
                new ContractTerminatedEvent(contract.getId(), request.terminationReason())
        );

        return contractMapper.toTerminateResponse(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public Contract getById(UUID id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CONTRACT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contract> getAll() {
        return contractRepository.findAll();
    }

}