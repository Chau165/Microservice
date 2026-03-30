package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.franchise.response.FranchiseWarehouseMappingItemResponse;
import com.group1.app.metadata.dto.franchise.response.WarehouseMappingResponse;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.FranchiseWarehouseMapping;
import com.group1.app.metadata.entity.franchise.FranchiseWarehouseMappingStatus;
import com.group1.app.metadata.event.franchise.WarehouseMappingChangedEvent;
import com.group1.app.metadata.infrastructure.WarehouseClient;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.repository.franchise.FranchiseWarehouseMappingRepository;
import com.group1.app.metadata.repository.franchise.OperationalConfigRepository;
import com.group1.app.metadata.service.FranchiseWarehouseMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FranchiseWarehouseMappingServiceImpl implements FranchiseWarehouseMappingService {

    private final FranchiseWarehouseMappingRepository warehouseMappingRepository;
    private final FranchiseRepository franchiseRepository;
    private final OperationalConfigRepository operationalConfigRepository;
    private final WarehouseClient warehouseClient;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public WarehouseMappingResponse createWarehouseMapping(UUID franchiseId, String warehouseId, String createdBy) {
        Franchise franchise = getValidatedFranchise(franchiseId);
        String normalizedWarehouseId = warehouseId.trim();

        Optional<FranchiseWarehouseMapping> activeMappingOpt =
                warehouseMappingRepository.findByFranchise_IdAndWarehouseIdAndStatus(
                        franchiseId,
                        normalizedWarehouseId,
                        FranchiseWarehouseMappingStatus.ACTIVE
                );

        if (activeMappingOpt.isPresent()) {
            throw new ApiException(ErrorCode.WM_003_ACTIVE_WAREHOUSE_MAPPING_EXISTS);
        }

        FranchiseWarehouseMapping newMapping = FranchiseWarehouseMapping.builder()
                .franchise(franchise)
                .warehouseId(normalizedWarehouseId)
                .status(FranchiseWarehouseMappingStatus.ACTIVE)
                .assignedAt(Instant.now())
                .build();
        warehouseMappingRepository.save(newMapping);

        markWarehouseMappingConfigured(franchiseId);

        eventPublisher.publishEvent(
                new WarehouseMappingChangedEvent(
                        franchiseId,
                        null,
                        normalizedWarehouseId,
                        createdBy,
                        LocalDateTime.now()
                )
        );

        return new WarehouseMappingResponse(
                franchiseId,
                normalizedWarehouseId,
                FranchiseWarehouseMappingStatus.ACTIVE.name(),
                newMapping.getAssignedAt()
        );
    }

    @Override
    public WarehouseMappingResponse updateWarehouseMapping(UUID franchiseId, String warehouseId, String changedBy) {
        Franchise franchise = getValidatedFranchise(franchiseId);
        String normalizedWarehouseId = warehouseId.trim();

        Optional<FranchiseWarehouseMapping> activeMappingOpt =
                warehouseMappingRepository.findByFranchise_IdAndWarehouseIdAndStatus(
                        franchiseId,
                        normalizedWarehouseId,
                        FranchiseWarehouseMappingStatus.ACTIVE
                );

        if (activeMappingOpt.isPresent()) {
            FranchiseWarehouseMapping activeMapping = activeMappingOpt.get();
            return new WarehouseMappingResponse(
                    franchiseId,
                    normalizedWarehouseId,
                    activeMapping.getStatus().name(),
                    activeMapping.getAssignedAt()
            );
        }

        FranchiseWarehouseMapping newMapping = FranchiseWarehouseMapping.builder()
                .franchise(franchise)
                .warehouseId(normalizedWarehouseId)
                .status(FranchiseWarehouseMappingStatus.ACTIVE)
                .assignedAt(Instant.now())
                .build();
        warehouseMappingRepository.save(newMapping);

        markWarehouseMappingConfigured(franchiseId);

        eventPublisher.publishEvent(
                new WarehouseMappingChangedEvent(
                        franchiseId,
                        null,
                        normalizedWarehouseId,
                        changedBy,
                        LocalDateTime.now()
                )
        );

        return new WarehouseMappingResponse(
                franchiseId,
                normalizedWarehouseId,
                FranchiseWarehouseMappingStatus.ACTIVE.name(),
                newMapping.getAssignedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FranchiseWarehouseMappingItemResponse> getAllByFranchiseId(UUID franchiseId) {

        if (!franchiseRepository.existsById(franchiseId)) {
            throw new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND);
        }

        return warehouseMappingRepository.findAllByFranchise_Id(franchiseId)
                .stream()
                .map(mapping -> new FranchiseWarehouseMappingItemResponse(
                        mapping.getId(),
                        mapping.getWarehouseId(),
                        mapping.getStatus().name(),
                        mapping.getAssignedAt(),
                        mapping.getUnassignedAt(),
                        mapping.getFranchise() != null ? mapping.getFranchise().getId() : null
                ))
                .toList();
    }

    private Franchise getValidatedFranchise(UUID franchiseId) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (franchise.getStatus() == null || franchise.getStatus().name().equals("SUSPENDED")) {
            throw new ApiException(ErrorCode.INVALID_FRANCHISE_STATUS);
        }

        return franchise;
    }

    private void markWarehouseMappingConfigured(UUID franchiseId) {
        operationalConfigRepository.findByFranchiseId(franchiseId)
                .ifPresent(config -> {
                    config.setWarehouseMappingConfigured(true);
                    operationalConfigRepository.save(config);
                });
    }
}
