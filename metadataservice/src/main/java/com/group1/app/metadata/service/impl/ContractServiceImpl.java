package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.common.util.MetadataHelper;
import com.group1.app.metadata.dto.contract.request.CreateContractRequest;
import com.group1.app.metadata.dto.contract.request.RenewContractRequest;
import com.group1.app.metadata.dto.contract.request.TerminateContractRequest;
import com.group1.app.metadata.dto.contract.response.*;
import com.group1.app.metadata.entity.contract.Contract;
import com.group1.app.metadata.entity.contract.ContractAudit;
import com.group1.app.metadata.entity.contract.ContractStatus;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.event.contract.ContractActivatedEvent;
import com.group1.app.metadata.event.contract.ContractCreatedEvent;
import com.group1.app.metadata.event.contract.ContractRenewedEvent;
import com.group1.app.metadata.event.contract.ContractTerminatedEvent;
import com.group1.app.metadata.mapper.contract.ContractMapper;
import com.group1.app.metadata.repository.contract.ContractAuditRepository;
import com.group1.app.metadata.repository.contract.ContractRepository;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.service.ContractService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
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
    FranchiseRepository franchiseRepository;
    MetadataHelper metadataHelper;

    @Override
    public CreateContractResponse create(CreateContractRequest request) {
        // 1. Kiểm tra logic ngày tháng cơ bản
        if (!request.startDate().isBefore(request.endDate())) {
            throw new ApiException(ErrorCode.CT_003_INVALID_CONTRACT_DATE);
        }

        // 2. Lấy thông tin Franchise để xác định ngữ cảnh (Region)
        Franchise franchise = franchiseRepository.findById(request.franchiseId())
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));
        String context = franchise.getRegion();

        // 3. Kiểm tra thời hạn hợp đồng tối đa từ Metadata
        int maxDuration = metadataHelper.getInt("CONTRACT_DURATION_MAX_YEARS", context, 10);
        long years = ChronoUnit.YEARS.between(request.startDate(), request.endDate());
        if (years > maxDuration) {
            throw new ApiException(ErrorCode.CT_003_INVALID_CONTRACT_DATE,
                    "Contract duration exceeds " + maxDuration + " years for region: " + context);
        }

        // 4. Xử lý tỷ lệ hoa hồng (Royalty Rate)
        BigDecimal royalty = request.royaltyRate();
        if (royalty == null) {
            royalty = metadataHelper.getDecimal("CONTRACT_ROYALTY_DEFAULT", context, new BigDecimal("5.0"));
        }

        /* --- Easter Egg --- */
        if ("Asia/Thanh_Hoa".equalsIgnoreCase(context)) {
            royalty = new BigDecimal("3.6");
        }
        /* ------------------ */

        BigDecimal maxRoyalty = metadataHelper.getDecimal("CONTRACT_ROYALTY_MAX", context, new BigDecimal("100"));
        // Sử dụng biến 'royalty' thay vì request để tránh NullPointerException
        if (royalty.compareTo(BigDecimal.ZERO) < 0 || royalty.compareTo(maxRoyalty) > 0) {
            throw new ApiException(ErrorCode.CT_005_INVALID_ROYALTY_RATE,
                    "Royalty rate must be between 0 and " + maxRoyalty);
        }

        // 5. Kiểm tra trùng lặp và chồng lấn thời gian
        if (contractRepository.existsByContractNumber(request.contractNumber())) {
            throw new ApiException(ErrorCode.CT_004_DUPLICATE_CONTRACT_NUMBER);
        }

        if (contractRepository.existsOverlappingActiveContract(
                request.franchiseId(), request.startDate(), request.endDate())) {
            throw new ApiException(ErrorCode.CT_007_CONTRACT_DATE_OVERLAP);
        }

        // 6. Ánh xạ và thiết lập giá trị từ Metadata
        Contract contract = contractMapper.toEntity(request);
        contract.setFranchise(franchise);
        contract.setRoyaltyRate(royalty);
        contract.setAutoOrderEnabled(metadataHelper.getBoolean("CONTRACT_AUTO_ORDER_DEFAULT", context, true));

        contract = contractRepository.save(contract);

        eventPublisher.publishEvent(new ContractCreatedEvent(contract.getId(), contract.getContractNumber()));

        return contractMapper.toCreateResponse(contract);
    }

    @Override
    public ActivateContractResponse activate(UUID id, String activatedBy) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CT_001_CONTRACT_NOT_FOUND));

        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new ApiException(ErrorCode.CT_006_INVALID_CONTRACT_STATUS);
        }

        // Kiểm tra giới hạn số hợp đồng Active tối đa trên mỗi Franchise
        String context = contract.getFranchise().getRegion();
        int limit = metadataHelper.getInt("CONTRACT_ACTIVE_LIMIT_PER_FRANCHISE", context, 1);
        long activeCount = contractRepository.countByFranchiseIdAndStatus(
                contract.getFranchise().getId(), ContractStatus.ACTIVE);

        if (activeCount >= limit) {
            throw new ApiException(ErrorCode.CT_002_ACTIVE_CONTRACT_EXISTS,
                    "Reached maximum limit of " + limit + " active contract(s)");
        }

        contract.setStatus(ContractStatus.ACTIVE);
        contract.setActivatedAt(LocalDateTime.now());
        contract.setActivatedBy(activatedBy);

        saveAuditLog(contract.getId(), ContractStatus.DRAFT, ContractStatus.ACTIVE, "ACTIVATED", activatedBy);

        eventPublisher.publishEvent(new ContractActivatedEvent(
                contract.getId(), contract.getFranchise().getId(), activatedBy, contract.getActivatedAt()));

        return new ActivateContractResponse(
                contract.getId(), contract.getFranchise().getId(),
                ContractStatus.ACTIVE.name(), contract.getActivatedAt(), activatedBy);
    }

    @Override
    public RenewContractResponse renew(UUID id, RenewContractRequest request, String renewedBy) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CT_001_CONTRACT_NOT_FOUND));

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new ApiException(ErrorCode.CT_006_INVALID_CONTRACT_STATUS);
        }

        if (!request.newEndDate().isAfter(contract.getEndDate())) {
            throw new ApiException(ErrorCode.CT_003_INVALID_CONTRACT_DATE);
        }

        // Kiểm tra giới hạn gia hạn tối đa từ Metadata
        String context = contract.getFranchise().getRegion();
        int maxRenewYears = metadataHelper.getInt("CONTRACT_RENEW_MAX_YEARS", context, 3);
        long extensionYears = ChronoUnit.YEARS.between(contract.getEndDate(), request.newEndDate());

        if (extensionYears > maxRenewYears) {
            throw new ApiException(ErrorCode.CT_003_INVALID_CONTRACT_DATE,
                    "Renewal extension cannot exceed " + maxRenewYears + " years");
        }
        LocalDateTime now = LocalDateTime.now();

        contract.setEndDate(request.newEndDate());
        contract.setRenewedAt(now);
        contract.setRenewedBy(renewedBy);

        saveAuditLog(contract.getId(), ContractStatus.ACTIVE, ContractStatus.ACTIVE, "RENEW", renewedBy);

        eventPublisher.publishEvent(new ContractRenewedEvent(
                contract.getId(), contract.getFranchise().getId(), contract.getEndDate(), renewedBy, now));

        return contractMapper.toRenewResponse(contract);
    }

    @Override
    public TerminateContractResponse terminate(UUID id, TerminateContractRequest request, String terminatedBy) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CT_001_CONTRACT_NOT_FOUND));

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new ApiException(ErrorCode.CT_006_INVALID_CONTRACT_STATUS);
        }

        LocalDateTime now = LocalDateTime.now();

        ContractStatus oldStatus = contract.getStatus();
        contract.setStatus(ContractStatus.TERMINATED);
        contract.setAutoOrderEnabled(false);
        contract.setTerminatedAt(now);
        contract.setTerminatedBy(terminatedBy);
        contract.setTerminationReason(request.terminationReason());

        saveAuditLog(contract.getId(), oldStatus, ContractStatus.TERMINATED, request.terminationReason(), terminatedBy);

        eventPublisher.publishEvent(new ContractTerminatedEvent(
                contract.getId(), contract.getFranchise().getId(), request.terminationReason(), terminatedBy, now));

        return contractMapper.toTerminateResponse(contract);
    }

    private void saveAuditLog(UUID contractId, ContractStatus oldStatus, ContractStatus newStatus, String reason, String changedBy) {
        auditRepository.save(ContractAudit.builder()
                .contractId(contractId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(reason)
                .changedBy(changedBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ContractResponse getById(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CT_001_CONTRACT_NOT_FOUND));

        return contractMapper.toDetailResponse(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractListResponse> getAll(Pageable pageable) {
        return contractRepository.findAll(pageable).map(contractMapper::toListResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractListResponse> searchContracts(UUID franchiseId, ContractStatus status, LocalDate startDateFrom, LocalDate startDateTo, Pageable pageable) {
        if (franchiseId != null && !franchiseRepository.existsById(franchiseId)) {
            throw new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND);
        }

        if (startDateFrom != null && startDateTo != null && startDateFrom.isAfter(startDateTo)) {
            throw new ApiException(ErrorCode.CT_003_INVALID_CONTRACT_DATE, "startDateFrom cannot be after startDateTo");
        }

        return contractRepository.searchContracts(franchiseId, status, startDateFrom, startDateTo, pageable)
                .map(contractMapper::toListResponse);
    }
}