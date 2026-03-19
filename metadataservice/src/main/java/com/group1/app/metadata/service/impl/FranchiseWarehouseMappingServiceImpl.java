package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
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
    public WarehouseMappingResponse updateWarehouseMapping(UUID franchiseId, String warehouseId, String changedBy) {

        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (franchise.getStatus().name().equals("SUSPENDED")) {
            throw new ApiException(ErrorCode.INVALID_FRANCHISE_STATUS);
        }

        var warehouse = warehouseClient.getWarehouseById(warehouseId);
        if (warehouse == null) {
            throw new ApiException(ErrorCode.WM_001_WAREHOUSE_NOT_FOUND);
        }

        String oldWarehouseId = null;

        var activeMappingOpt = warehouseMappingRepository.findByFranchise_IdAndStatus(
                franchiseId,
                FranchiseWarehouseMappingStatus.ACTIVE
        );

        if (activeMappingOpt.isPresent()) {
            FranchiseWarehouseMapping oldMapping = activeMappingOpt.get();
            oldWarehouseId = oldMapping.getWarehouseId();

            // No change — return current mapping as-is
            if (oldWarehouseId.equals(warehouseId)) {
                return new WarehouseMappingResponse(
                        franchiseId,
                        warehouseId,
                        oldMapping.getStatus().name(),
                        oldMapping.getAssignedAt()
                );
            }

            oldMapping.setStatus(FranchiseWarehouseMappingStatus.INACTIVE);
            oldMapping.setUnassignedAt(Instant.now());
            warehouseMappingRepository.save(oldMapping);
        }

        FranchiseWarehouseMapping newMapping = FranchiseWarehouseMapping.builder()
                .franchise(franchise)
                .warehouseId(warehouseId)
                .status(FranchiseWarehouseMappingStatus.ACTIVE)
                .assignedAt(Instant.now())
                .build();
        warehouseMappingRepository.save(newMapping);

        // Update OperationalConfig flag
        operationalConfigRepository.findByFranchiseId(franchiseId)
                .ifPresent(config -> {
                    config.setWarehouseMappingConfigured(true);
                    operationalConfigRepository.save(config);
                });

        eventPublisher.publishEvent(
                new WarehouseMappingChangedEvent(
                        franchiseId,
                        oldWarehouseId,
                        warehouseId,
                        changedBy,
                        LocalDateTime.now()
                )
        );

        return new WarehouseMappingResponse(
                franchiseId,
                warehouseId,
                FranchiseWarehouseMappingStatus.ACTIVE.name(),
                newMapping.getAssignedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FranchiseWarehouseMapping> getAllByFranchiseId(UUID franchiseId) {

        if (!franchiseRepository.existsById(franchiseId)) {
            throw new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND);
        }

        return warehouseMappingRepository.findAllByFranchise_Id(franchiseId);
    }
}
