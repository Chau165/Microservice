package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.FranchiseWarehouseMapping;
import com.group1.app.metadata.entity.franchise.FranchiseWarehouseMappingStatus;
import com.group1.app.metadata.infrastructure.WarehouseClient;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.repository.franchise.FranchiseWarehouseMappingRepository;
import com.group1.app.metadata.repository.franchise.OperationalConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseWarehouseMappingServiceImplTest {

    @InjectMocks
    FranchiseWarehouseMappingServiceImpl service;

    @Mock
    FranchiseWarehouseMappingRepository warehouseMappingRepository;

    @Mock
    FranchiseRepository franchiseRepository;

    @Mock
    OperationalConfigRepository operationalConfigRepository;

    @Mock
    com.fasterxml.jackson.databind.JsonNode warehouseNode;

    @Mock
    WarehouseClient warehouseClient;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Test
    void updateWarehouseMapping_franchiseNotFound() {
        UUID id = UUID.randomUUID();
        when(franchiseRepository.findById(id)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.updateWarehouseMapping(id, "w", "u"));
        assertEquals(ErrorCode.FR_404_FRANCHISE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateWarehouseMapping_createsAdditionalActiveMappingWhenWarehouseIsNew() {
        UUID id = UUID.randomUUID();
        Franchise franchise = new Franchise();
        franchise.setId(id);
        franchise.setStatus(com.group1.app.metadata.entity.franchise.FranchiseStatus.LIVE);

        when(franchiseRepository.findById(id)).thenReturn(Optional.of(franchise));
        when(warehouseMappingRepository.findByFranchise_IdAndWarehouseIdAndStatus(
                id,
                "w",
                FranchiseWarehouseMappingStatus.ACTIVE
        )).thenReturn(Optional.empty());
        when(warehouseMappingRepository.save(any(FranchiseWarehouseMapping.class))).thenAnswer(i -> i.getArgument(0));

        var resp = service.updateWarehouseMapping(id, "w", "u");

        assertNotNull(resp);
        assertEquals("w", resp.warehouseId());
    }

    @Test
    void updateWarehouseMapping_sameWarehouseAlreadyActive_returnsExistingMapping() {
        UUID id = UUID.randomUUID();
        Franchise franchise = new Franchise();
        franchise.setId(id);
        franchise.setStatus(com.group1.app.metadata.entity.franchise.FranchiseStatus.LIVE);

        FranchiseWarehouseMapping existing = new FranchiseWarehouseMapping();
        existing.setWarehouseId("w1");
        existing.setStatus(FranchiseWarehouseMappingStatus.ACTIVE);

        when(franchiseRepository.findById(id)).thenReturn(Optional.of(franchise));
        when(warehouseMappingRepository.findByFranchise_IdAndWarehouseIdAndStatus(
                id,
                "w1",
                FranchiseWarehouseMappingStatus.ACTIVE
        )).thenReturn(Optional.of(existing));

        var resp = service.updateWarehouseMapping(id, "w1", "u");

        assertNotNull(resp);
        assertEquals("w1", resp.warehouseId());
        verify(warehouseMappingRepository, never()).save(any(FranchiseWarehouseMapping.class));
    }

    @Test
    void createWarehouseMapping_allowsMultipleActiveWarehousesForSameFranchise() {
        UUID id = UUID.randomUUID();
        Franchise franchise = new Franchise();
        franchise.setId(id);
        franchise.setStatus(com.group1.app.metadata.entity.franchise.FranchiseStatus.LIVE);

        when(franchiseRepository.findById(id)).thenReturn(Optional.of(franchise));
        when(warehouseMappingRepository.findByFranchise_IdAndWarehouseIdAndStatus(
                id,
                "w2",
                FranchiseWarehouseMappingStatus.ACTIVE
        )).thenReturn(Optional.empty());
        when(warehouseMappingRepository.save(any(FranchiseWarehouseMapping.class))).thenAnswer(i -> i.getArgument(0));

        var resp = service.createWarehouseMapping(id, "w2", "u");

        assertNotNull(resp);
        assertEquals("w2", resp.warehouseId());
        verify(warehouseMappingRepository, never()).findByFranchise_IdAndStatus(any(), any());
    }

    @Test
    void createWarehouseMapping_duplicateActiveSameWarehouse_throwsConflict() {
        UUID id = UUID.randomUUID();
        Franchise franchise = new Franchise();
        franchise.setId(id);
        franchise.setStatus(com.group1.app.metadata.entity.franchise.FranchiseStatus.LIVE);

        FranchiseWarehouseMapping existing = new FranchiseWarehouseMapping();
        existing.setWarehouseId("w1");
        existing.setStatus(FranchiseWarehouseMappingStatus.ACTIVE);

        when(franchiseRepository.findById(id)).thenReturn(Optional.of(franchise));
        when(warehouseMappingRepository.findByFranchise_IdAndWarehouseIdAndStatus(
                id,
                "w1",
                FranchiseWarehouseMappingStatus.ACTIVE
        )).thenReturn(Optional.of(existing));

        ApiException ex = assertThrows(ApiException.class, () -> service.createWarehouseMapping(id, "w1", "u"));
        assertEquals(ErrorCode.WM_003_ACTIVE_WAREHOUSE_MAPPING_EXISTS, ex.getErrorCode());
    }

    @Test
    void getAllByFranchiseId_notFound() {
        UUID franchiseId = UUID.randomUUID();
        when(franchiseRepository.existsById(franchiseId)).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class, () -> service.getAllByFranchiseId(franchiseId));
        assertEquals(ErrorCode.FR_404_FRANCHISE_NOT_FOUND, ex.getErrorCode());
        verify(warehouseMappingRepository, never()).findAllByFranchise_Id(any());
    }

    @Test
    void getAllByFranchiseId_success() {
        UUID franchiseId = UUID.randomUUID();
        FranchiseWarehouseMapping mapping = new FranchiseWarehouseMapping();
        mapping.setWarehouseId("w1");
        when(franchiseRepository.existsById(franchiseId)).thenReturn(true);
        when(warehouseMappingRepository.findAllByFranchise_Id(franchiseId)).thenReturn(List.of(mapping));

        var result = service.getAllByFranchiseId(franchiseId);

        assertEquals(1, result.size());
        assertEquals("w1", result.get(0).getWarehouseId());
    }
}
