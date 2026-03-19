package com.group1.app.metadata.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.entity.franchise.*;
import com.group1.app.metadata.event.franchise.*;
import com.group1.app.metadata.infrastructure.SupplierClient;
import com.group1.app.metadata.mapper.franchise.FranchiseMapper;
import com.group1.app.metadata.dto.franchise.request.CreateFranchiseRequest;
import com.group1.app.metadata.dto.franchise.request.UpdateFranchiseRequest;
import com.group1.app.metadata.dto.franchise.response.FranchiseResponse;
import com.group1.app.metadata.entity.contract.ContractStatus;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaffStatus;
import com.group1.app.metadata.repository.contract.ContractRepository;
import com.group1.app.metadata.repository.franchise.FranchiseAuditRepository;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.repository.franchise.FranchiseSupplierRepository;
import com.group1.app.metadata.repository.franchise.OperationalConfigRepository;
import com.group1.app.metadata.service.FranchiseService;
import com.group1.app.shift.entity.Staff;
import com.group1.app.shift.repository.StaffRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class FranchiseServiceImpl implements FranchiseService {

    FranchiseRepository franchiseRepository;
    OperationalConfigRepository configRepository;
    FranchiseAuditRepository auditRepository;
    ContractRepository contractRepository;
    FranchiseMapper franchiseMapper;
    com.group1.app.metadata.repository.franchise.FranchiseOpeningHourRepository openingHourRepository;
    ApplicationEventPublisher eventPublisher;
    FranchiseSupplierRepository franchiseSupplierRepository;
    SupplierClient supplierClient;
        StaffRepository staffRepository;

    @Override
    public FranchiseResponse create(CreateFranchiseRequest request) {

        if (franchiseRepository.existsByFranchiseCode(request.franchiseCode())) {
            throw new ApiException(ErrorCode.FRANCHISE_ALREADY_EXISTS);
        }

        if (!ZoneId.getAvailableZoneIds().contains(request.timezone())) {
            throw new ApiException(ErrorCode.INVALID_TIMEZONE);
        }

        Franchise franchise = franchiseMapper.toEntity(request);
        franchise = franchiseRepository.save(franchise);

        OperationalConfig cfg = OperationalConfig.builder()
                .franchiseId(franchise.getId())
                .openingHoursConfigured(false)
                .menuProfileAssigned(false)
                .warehouseMappingConfigured(false)
                .build();
        configRepository.save(cfg);

        auditRepository.save(
                FranchiseAudit.builder()
                        .franchiseId(franchise.getId())
                        .fieldChanged("CREATION")
                        .oldValue(null)
                        .newValue("Franchise created: " + franchise.getFranchiseCode())
                        .changedBy(franchise.getCreatedBy())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        eventPublisher.publishEvent(new FranchiseCreatedEvent(franchise.getId(), franchise.getFranchiseCode()));

        return franchiseMapper.toResponse(franchise);
    }

    @Override
    @Transactional(readOnly = true)
    public FranchiseResponse getById(UUID id) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));
        return franchiseMapper.toResponse(franchise);
    }

    @Override
    public FranchiseResponse updateIdentity(UUID id, UpdateFranchiseRequest request, String changedBy) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (!ZoneId.getAvailableZoneIds().contains(request.timezone())) {
            throw new ApiException(ErrorCode.INVALID_TIMEZONE);
        }

        String old = franchise.toString();

        franchise.setFranchiseName(request.franchiseName());
        franchise.setAddress(request.address());
        franchise.setRegion(request.region());
        franchise.setContactInfo(request.contactInfo());
        franchise.setTimezone(request.timezone());

        franchise = franchiseRepository.save(franchise);

        auditRepository.save(
                FranchiseAudit.builder()
                        .franchiseId(franchise.getId())
                        .fieldChanged("IDENTITY_UPDATE")
                        .oldValue(old)
                        .newValue(franchise.toString())
                        .changedBy(changedBy)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return franchiseMapper.toResponse(franchise);
    }

    @Override
    public void activate(UUID id, String activatedBy) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (!(franchise.getStatus() == FranchiseStatus.PENDING || franchise.getStatus() == FranchiseStatus.SUSPENDED)) {
            throw new ApiException(ErrorCode.INVALID_FRANCHISE_STATUS);
        }

        boolean hasActiveContract = contractRepository.existsByFranchiseIdAndStatus(franchise.getId(), ContractStatus.ACTIVE);
        if (!hasActiveContract) {
            throw new ApiException(ErrorCode.CANNOT_ACTIVATE_NO_ACTIVE_CONTRACT);
        }

        Optional<OperationalConfig> cfgOpt = configRepository.findByFranchiseId(franchise.getId());
        if (cfgOpt.isEmpty()) {
            throw new ApiException(ErrorCode.OPERATIONAL_CONFIG_INCOMPLETE);
        }
        OperationalConfig cfg = cfgOpt.get();
        if (!(cfg.isOpeningHoursConfigured() && cfg.isMenuProfileAssigned() && cfg.isWarehouseMappingConfigured())) {
            throw new ApiException(ErrorCode.OPERATIONAL_CONFIG_INCOMPLETE);
        }

        FranchiseStatus oldStatus = franchise.getStatus();
        franchise.setStatus(FranchiseStatus.LIVE);
        franchiseRepository.save(franchise);

        auditRepository.save(
                FranchiseAudit.builder()
                        .franchiseId(franchise.getId())
                        .fieldChanged("STATUS_CHANGE")
                        .oldValue(oldStatus.name())
                        .newValue(FranchiseStatus.LIVE.name())
                        .changedBy(activatedBy)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        eventPublisher.publishEvent(new FranchiseActivatedEvent(franchise.getId(), activatedBy, LocalDateTime.now()));
    }

    @Override
    public void deactivate(UUID id, String deactivatedBy) {

        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (franchise.getStatus() != FranchiseStatus.LIVE) {
            throw new ApiException(ErrorCode.INVALID_FRANCHISE_STATUS);
        }

        FranchiseStatus oldStatus = franchise.getStatus();
        franchise.setStatus(FranchiseStatus.SUSPENDED);

        franchiseRepository.save(franchise);

        auditRepository.save(
                FranchiseAudit.builder()
                        .franchiseId(franchise.getId())
                        .fieldChanged("STATUS_CHANGE")
                        .oldValue(oldStatus.name())
                        .newValue(FranchiseStatus.SUSPENDED.name())
                        .changedBy(deactivatedBy)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        eventPublisher.publishEvent(
                new FranchiseActivatedEvent(
                        franchise.getId(),
                        deactivatedBy,
                        LocalDateTime.now()
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FranchiseResponse> getAllByManager(String managerUserId, Pageable pageable) {

        Optional<Staff> staffOpt = staffRepository.findByUserId(managerUserId);
        if (staffOpt.isEmpty()) {
            return Page.empty(pageable);
        }

        String staffId = staffOpt.get().getId();

        Page<Franchise> franchises = franchiseRepository
                .findDistinctByFranchiseStaffs_StaffIdAndFranchiseStaffs_Status(
                        staffId,
                        FranchiseStaffStatus.ACTIVE,
                        pageable
                );

        return franchises.map(franchiseMapper::toResponse);
    }

    @Override
    public void suspend(UUID franchiseId, String reason, String suspendedBy) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (franchise.getStatus() != FranchiseStatus.LIVE) {
            throw new ApiException(ErrorCode.INVALID_FRANCHISE_STATUS);
        }

        FranchiseStatus oldStatus = franchise.getStatus();

        franchise.setStatus(FranchiseStatus.SUSPENDED);
        franchiseRepository.save(franchise);

        OperationalConfig config = configRepository
                .findByFranchiseId(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.OPERATIONAL_CONFIG_INCOMPLETE));

        config.setPosEnabled(false);
        config.setOrderingEnabled(false);
        config.setAutoOrderEnabled(false);

        configRepository.save(config);

        auditRepository.save(
                FranchiseAudit.builder()
                        .franchiseId(franchiseId)
                        .fieldChanged("STATUS_CHANGE")
                        .oldValue(oldStatus.name())
                        .newValue(FranchiseStatus.SUSPENDED.name())
                        .changedBy(suspendedBy)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        eventPublisher.publishEvent(
                new FranchiseSuspendedEvent(
                        franchiseId,
                        reason,
                        suspendedBy,
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public void assignOwner(UUID franchiseId, UUID ownerId, String role, LocalDate effectiveDate, String assignedBy) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (franchise.getOwnerId() != null) {
            throw new ApiException(ErrorCode.FRANCHISE_ALREADY_HAS_OWNER);
        }

        if (effectiveDate.isBefore(LocalDate.now())) {
            throw new ApiException(ErrorCode.INVALID_EFFECTIVE_DATE);
        }

        UUID oldOwner = franchise.getOwnerId();

        franchise.setOwnerId(ownerId);

        franchiseRepository.save(franchise);

        auditRepository.save(
                FranchiseAudit.builder()
                        .franchiseId(franchiseId)
                        .fieldChanged("OWNER_ASSIGNMENT")
                        .oldValue(oldOwner == null ? "null" : oldOwner.toString())
                        .newValue(ownerId.toString())
                        .changedBy(assignedBy)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        eventPublisher.publishEvent(
                new FranchiseOwnerAssignedEvent(
                        franchiseId,
                        ownerId,
                        role,
                        effectiveDate,
                        assignedBy,
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public void changeOwner(UUID franchiseId, UUID newOwnerId, String role, LocalDate effectiveDate, String changedBy) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (franchise.getOwnerId() == null) {
            throw new ApiException(ErrorCode.FRANCHISE_HAS_NO_OWNER);
        }

        UUID oldOwner = franchise.getOwnerId();

        if (oldOwner.equals(newOwnerId)) {
            throw new ApiException(ErrorCode.INVALID_OWNER_TRANSFER);
        }

        if (effectiveDate.isBefore(LocalDate.now())) {
            throw new ApiException(ErrorCode.INVALID_EFFECTIVE_DATE);
        }

        franchise.setOwnerId(newOwnerId);

        franchiseRepository.save(franchise);

        auditRepository.save(
                FranchiseAudit.builder()
                        .franchiseId(franchiseId)
                        .fieldChanged("OWNER_TRANSFER")
                        .oldValue(oldOwner.toString())
                        .newValue(newOwnerId.toString())
                        .changedBy(changedBy)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        eventPublisher.publishEvent(
                new FranchiseOwnershipTransferredEvent(
                        franchiseId,
                        oldOwner,
                        newOwnerId,
                        role,
                        effectiveDate,
                        changedBy,
                        LocalDateTime.now()
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FranchiseResponse> getAll() {

        List<Franchise> franchises = franchiseRepository
                .findAll();

        return franchises.stream()
                .map(franchiseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public com.group1.app.metadata.dto.franchise.response.FranchiseConfigurationResponse getConfiguration(UUID franchiseId) {

        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        OperationalConfig config = configRepository
                .findByFranchiseId(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.OPERATIONAL_CONFIG_INCOMPLETE));

        java.util.List<com.group1.app.metadata.dto.franchise.response.OpeningHourResponse> openingHours =
                openingHourRepository.findByFranchise_Id(franchiseId)
                        .stream()
                        .map(hour -> new com.group1.app.metadata.dto.franchise.response.OpeningHourResponse(
                                franchiseId,
                                hour.getDayOfWeek(),
                                hour.getOpenTime(),
                                hour.getCloseTime(),
                                hour.getIsClosed()
                        ))
                        .toList();

        return com.group1.app.metadata.dto.franchise.response.FranchiseConfigurationResponse.builder()
                .featureFlags(franchise.isFeatureFlags())
                .openingHours(openingHours)
                .menuProfileAssigned(config.isMenuProfileAssigned())
                .warehouseMappingConfigured(config.isWarehouseMappingConfigured())
                .posEnabled(config.isPosEnabled())
                .orderingEnabled(config.isOrderingEnabled())
                .autoOrderEnabled(config.isAutoOrderEnabled())
                .build();
    }

    @Override
    public void assignMenuProfile(UUID franchiseId, UUID menuProfileId, String assignedBy) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        OperationalConfig config = configRepository
                .findByFranchiseId(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.OPERATIONAL_CONFIG_INCOMPLETE));

        UUID previousMenuProfile = config.getMenuProfileId();

        if (previousMenuProfile == null && franchise.getStatus() == FranchiseStatus.LIVE) {
            throw new ApiException(ErrorCode.INVALID_FRANCHISE_STATUS);
        }

        config.setMenuProfileId(menuProfileId);
        config.setMenuProfileAssigned(true);

        configRepository.save(config);

        auditRepository.save(
                FranchiseAudit.builder()
                        .franchiseId(franchiseId)
                        .fieldChanged("MENU_PROFILE_ASSIGNMENT")
                        .oldValue(previousMenuProfile == null ? "null" : previousMenuProfile.toString())
                        .newValue(menuProfileId.toString())
                        .changedBy(assignedBy)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        eventPublisher.publishEvent(
                new MenuProfileAssignedEvent(
                        franchiseId,
                        menuProfileId,
                        previousMenuProfile,
                        assignedBy,
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public void approveSupplierList(UUID franchiseId, List<UUID> supplierIds, String approvedBy) {
        franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (supplierIds == null || supplierIds.isEmpty()) {
            throw new ApiException(ErrorCode.SUPPLIER_NOT_FOUND);
        }

        List<UUID> distinctSuppliers = supplierIds.stream().distinct().toList();

        if (distinctSuppliers.size() != supplierIds.size()) {
            throw new ApiException(ErrorCode.SUPPLIER_NOT_FOUND);
        }

        for (UUID supplierId : distinctSuppliers) {

            JsonNode supplier = supplierClient.getSupplierById(supplierId.toString());

            if (supplier == null || supplier.isEmpty()) {
                throw new ApiException(ErrorCode.SUPPLIER_NOT_FOUND);
            }

            boolean exists = franchiseSupplierRepository
                    .existsByFranchiseIdAndSupplierId(franchiseId, supplierId);

            if (!exists) {

                FranchiseSupplier mapping = FranchiseSupplier.builder()
                        .franchiseId(franchiseId)
                        .supplierId(supplierId)
                        .approvedBy(approvedBy)
                        .approvedAt(LocalDateTime.now())
                        .build();

                franchiseSupplierRepository.save(mapping);
            }
        }

        eventPublisher.publishEvent(
                new SupplierListApprovedEvent(
                        franchiseId,
                        distinctSuppliers,
                        approvedBy,
                        LocalDateTime.now()
                )
        );
    }
}
