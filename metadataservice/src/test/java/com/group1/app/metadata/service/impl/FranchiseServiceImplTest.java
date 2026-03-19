package com.group1.app.metadata.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.franchise.request.CreateFranchiseRequest;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.FranchiseStatus;
import com.group1.app.metadata.entity.franchise.OperationalConfig;
import com.group1.app.metadata.infrastructure.SupplierClient;
import com.group1.app.metadata.mapper.franchise.FranchiseMapper;
import com.group1.app.metadata.repository.contract.ContractRepository;
import com.group1.app.metadata.repository.franchise.*;
import com.group1.app.shift.repository.StaffRepository;
import com.group1.app.shift.entity.Staff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceImplTest {

    @InjectMocks
    FranchiseServiceImpl service;

    @Mock
    FranchiseRepository franchiseRepository;

    @Mock
    OperationalConfigRepository configRepository;

    @Mock
    FranchiseAuditRepository auditRepository;

    @Mock
    ContractRepository contractRepository;

    @Mock
    FranchiseMapper franchiseMapper;

    @Mock
    FranchiseOpeningHourRepository openingHourRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    FranchiseSupplierRepository franchiseSupplierRepository;

    @Mock
    SupplierClient supplierClient;

    @Mock
    StaffRepository staffRepository;

    @Test
    void create_duplicateCode() {
        CreateFranchiseRequest req = new CreateFranchiseRequest("Name", "CODE1", "Addr", "Region", "UTC", null, "c");
        when(franchiseRepository.existsByFranchiseCode("CODE1")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> service.create(req));
        assertEquals(ErrorCode.FRANCHISE_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void create_invalidTimezone() {
        CreateFranchiseRequest req = new CreateFranchiseRequest("Name", "CODE2", "Addr", "Region", "Invalid/Zone", null, "c");
        when(franchiseRepository.existsByFranchiseCode("CODE2")).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class, () -> service.create(req));
        assertEquals(ErrorCode.INVALID_TIMEZONE, ex.getErrorCode());
    }

    @Test
    void create_success() {
        CreateFranchiseRequest req = new CreateFranchiseRequest("Name", "CODE3", "Addr", "Region", "UTC", null, "c");
        when(franchiseRepository.existsByFranchiseCode("CODE3")).thenReturn(false);
        Franchise f = new Franchise();
        f.setFranchiseCode("CODE3");
        when(franchiseMapper.toEntity(req)).thenReturn(f);
        when(franchiseRepository.save(any())).thenAnswer(invocation -> {
            Franchise saved = invocation.getArgument(0);
            // simulate DB generated id
            saved.setId(UUID.randomUUID());
            return saved;
        });

        when(franchiseMapper.toResponse(f)).thenReturn(new com.group1.app.metadata.dto.franchise.response.FranchiseResponse(
                UUID.randomUUID(), "Name", "CODE3", "Addr", "Region", "UTC",
                FranchiseStatus.PENDING,
                com.group1.app.metadata.entity.franchise.OnboardingStatus.PENDING,
                false, null, "c"
        ));

        var resp = service.create(req);
        assertNotNull(resp);
        verify(configRepository, times(1)).save(any(OperationalConfig.class));
        verify(auditRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.FranchiseCreatedEvent.class));
    }

    @Test
    void activate_noActiveContract() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setStatus(FranchiseStatus.PENDING);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        doReturn(false).when(contractRepository).existsByFranchiseIdAndStatus(eq(id), any());

        ApiException ex = assertThrows(ApiException.class, () -> service.activate(id, "u"));
        assertEquals(ErrorCode.CANNOT_ACTIVATE_NO_ACTIVE_CONTRACT, ex.getErrorCode());
    }

    @Test
    void activate_operationalConfigIncomplete() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setStatus(FranchiseStatus.PENDING);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        doReturn(true).when(contractRepository).existsByFranchiseIdAndStatus(eq(id), any());
        when(configRepository.findByFranchiseId(id)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.activate(id, "u"));
        assertEquals(ErrorCode.OPERATIONAL_CONFIG_INCOMPLETE, ex.getErrorCode());
    }

    @Test
    void activate_success() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setStatus(FranchiseStatus.PENDING);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        doReturn(true).when(contractRepository).existsByFranchiseIdAndStatus(eq(id), any());

        OperationalConfig cfg = new OperationalConfig();
        cfg.setFranchiseId(id);
        cfg.setOpeningHoursConfigured(true);
        cfg.setMenuProfileAssigned(true);
        cfg.setWarehouseMappingConfigured(true);

        when(configRepository.findByFranchiseId(id)).thenReturn(Optional.of(cfg));

        service.activate(id, "u");

        verify(franchiseRepository, times(1)).save(any());
        verify(auditRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.FranchiseActivatedEvent.class));
    }

    @Test
    void assignOwner_alreadyHasOwner() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setOwnerId(UUID.randomUUID());
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        ApiException ex = assertThrows(ApiException.class, () -> service.assignOwner(id, UUID.randomUUID(), "r", LocalDate.now().plusDays(1), "u"));
        assertEquals(ErrorCode.FRANCHISE_ALREADY_HAS_OWNER, ex.getErrorCode());
    }

    @Test
    void assignOwner_effectiveDatePast() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setOwnerId(null);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        ApiException ex = assertThrows(ApiException.class, () -> service.assignOwner(id, UUID.randomUUID(), "r", LocalDate.now().minusDays(1), "u"));
        assertEquals(ErrorCode.INVALID_EFFECTIVE_DATE, ex.getErrorCode());
    }

    @Test
    void changeOwner_noOwner() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setOwnerId(null);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        ApiException ex = assertThrows(ApiException.class, () -> service.changeOwner(id, UUID.randomUUID(), "r", LocalDate.now().plusDays(1), "u"));
        assertEquals(ErrorCode.FRANCHISE_HAS_NO_OWNER, ex.getErrorCode());
    }

    @Test
    void changeOwner_sameOwner() {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setOwnerId(owner);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        ApiException ex = assertThrows(ApiException.class, () -> service.changeOwner(id, owner, "r", LocalDate.now().plusDays(1), "u"));
        assertEquals(ErrorCode.INVALID_OWNER_TRANSFER, ex.getErrorCode());
    }

    @Test
    void changeOwner_effectiveDatePast() {
        UUID id = UUID.randomUUID();
        UUID oldOwner = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setOwnerId(oldOwner);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        ApiException ex = assertThrows(ApiException.class, () -> service.changeOwner(id, UUID.randomUUID(), "r", LocalDate.now().minusDays(1), "u"));
        assertEquals(ErrorCode.INVALID_EFFECTIVE_DATE, ex.getErrorCode());
    }

    @Test
    void getById_success() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        when(franchiseMapper.toResponse(f)).thenReturn(new com.group1.app.metadata.dto.franchise.response.FranchiseResponse(
                id, "N", "C", "A", "R", "UTC",
                FranchiseStatus.PENDING, com.group1.app.metadata.entity.franchise.OnboardingStatus.PENDING, false, null, "c"
        ));

        var resp = service.getById(id);
        assertNotNull(resp);
    }

    @Test
    void updateIdentity_success() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        when(franchiseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var req = new com.group1.app.metadata.dto.franchise.request.UpdateFranchiseRequest("Nm", "Addr", "Region", "Contact", "UTC");
        when(franchiseMapper.toResponse(any())).thenReturn(new com.group1.app.metadata.dto.franchise.response.FranchiseResponse(
                id, "Nm", "C", "Addr", "Region", "UTC",
                FranchiseStatus.PENDING, com.group1.app.metadata.entity.franchise.OnboardingStatus.PENDING, false, null, "c"
        ));

        var resp = service.updateIdentity(id, req, "u");
        assertNotNull(resp);
        verify(auditRepository, times(1)).save(any());
    }

    @Test
    void getAllByManager_success() {
        String managerUserId = UUID.randomUUID().toString();
        String staffId = "staff-123";
        
        // Create a Staff entity
        Staff staff = new Staff();
        staff.setId(staffId);
        staff.setUserId(managerUserId);
        
        // Create franchise
        Franchise f = new Franchise();
        f.setId(UUID.randomUUID());
        Page<Franchise> page = new PageImpl<>(List.of(f));
        
        // Mock staffRepository to return the staff
        when(staffRepository.findByUserId(managerUserId)).thenReturn(Optional.of(staff));
        
        // Mock franchiseRepository to return franchises
        when(franchiseRepository.findDistinctByFranchiseStaffs_StaffIdAndFranchiseStaffs_Status(
                eq(staffId), 
                any(), 
                any(Pageable.class)
        )).thenReturn(page);
        
        when(franchiseMapper.toResponse(any())).thenReturn(new com.group1.app.metadata.dto.franchise.response.FranchiseResponse(
                f.getId(), "N", "C", "A", "R", "UTC",
                FranchiseStatus.PENDING, com.group1.app.metadata.entity.franchise.OnboardingStatus.PENDING, false, null, "c"
        ));

        var res = service.getAllByManager(managerUserId, PageRequest.of(0, 10));
        assertEquals(1, res.getTotalElements());
    }

    @Test
    void getAllByManager_staffNotFound() {
        String managerUserId = UUID.randomUUID().toString();
        
        // Mock staffRepository to return empty
        when(staffRepository.findByUserId(managerUserId)).thenReturn(Optional.empty());

        var res = service.getAllByManager(managerUserId, PageRequest.of(0, 10));
        assertEquals(0, res.getTotalElements());
        assertTrue(res.isEmpty());
    }

    @Test
    void getAll_success() {
        Franchise f = new Franchise(); f.setId(UUID.randomUUID());
        when(franchiseRepository.findAll()).thenReturn(List.of(f));
        when(franchiseMapper.toResponse(any())).thenReturn(new com.group1.app.metadata.dto.franchise.response.FranchiseResponse(
                f.getId(), "N", "C", "A", "R", "UTC",
                FranchiseStatus.PENDING, com.group1.app.metadata.entity.franchise.OnboardingStatus.PENDING, false, null, "c"
        ));

        var all = service.getAll();
        assertEquals(1, all.size());
    }

    @Test
    void getConfiguration_success() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        OperationalConfig cfg = new OperationalConfig();
        cfg.setFranchiseId(id);
        cfg.setMenuProfileAssigned(true);
        cfg.setWarehouseMappingConfigured(true);
        cfg.setPosEnabled(true);
        cfg.setOrderingEnabled(true);
        cfg.setAutoOrderEnabled(true);
        when(configRepository.findByFranchiseId(id)).thenReturn(Optional.of(cfg));

        when(openingHourRepository.findByFranchise_Id(id)).thenReturn(List.of());

        var config = service.getConfiguration(id);
        assertNotNull(config);
        assertTrue(config.isMenuProfileAssigned());
    }

    @Test
    void getConfiguration_missingOpConfig() {
        UUID id = UUID.randomUUID();
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(new Franchise()));
        when(configRepository.findByFranchiseId(id)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.getConfiguration(id));
        assertEquals(ErrorCode.OPERATIONAL_CONFIG_INCOMPLETE, ex.getErrorCode());
    }

    @Test
    void assignMenuProfile_invalidState() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setStatus(FranchiseStatus.LIVE);
        OperationalConfig cfg = new OperationalConfig(); cfg.setMenuProfileId(null);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        when(configRepository.findByFranchiseId(id)).thenReturn(Optional.of(cfg));

        ApiException ex = assertThrows(ApiException.class, () -> service.assignMenuProfile(id, UUID.randomUUID(), "u"));
        assertEquals(ErrorCode.INVALID_FRANCHISE_STATUS, ex.getErrorCode());
    }

    @Test
    void assignMenuProfile_success() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setStatus(FranchiseStatus.PENDING);
        OperationalConfig cfg = new OperationalConfig(); cfg.setMenuProfileId(null);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        when(configRepository.findByFranchiseId(id)).thenReturn(Optional.of(cfg));

        service.assignMenuProfile(id, UUID.randomUUID(), "u");

        verify(configRepository, times(1)).save(any());
        verify(auditRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.MenuProfileAssignedEvent.class));
    }

    @Test
    void approveSupplierList_missingSuppliers() {
        UUID id = UUID.randomUUID();
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(new Franchise()));
        ApiException ex = assertThrows(ApiException.class, () -> service.approveSupplierList(id, List.of(), "u"));
        assertEquals(ErrorCode.SUPPLIER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void approveSupplierList_success() {
        UUID franchiseId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(franchiseId);
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(f));

        JsonNode supplier = mock(JsonNode.class);
        when(supplier.isEmpty()).thenReturn(false);
        when(supplierClient.getSupplierById(eq(supplierId.toString()))).thenReturn(supplier);
        when(franchiseSupplierRepository.existsByFranchiseIdAndSupplierId(franchiseId, supplierId)).thenReturn(false);

        service.approveSupplierList(franchiseId, List.of(supplierId), "u");

        verify(franchiseSupplierRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.SupplierListApprovedEvent.class));
    }

    @Test
    void suspend_success() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setStatus(FranchiseStatus.LIVE);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        OperationalConfig cfg = new OperationalConfig(); cfg.setFranchiseId(id);
        when(configRepository.findByFranchiseId(id)).thenReturn(Optional.of(cfg));

        service.suspend(id, "reason", "u");

        verify(franchiseRepository, times(1)).save(any());
        verify(configRepository, times(1)).save(any());
        verify(auditRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.FranchiseSuspendedEvent.class));
    }

    @Test
    void assignOwner_success() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setOwnerId(null);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        UUID owner = UUID.randomUUID();
        service.assignOwner(id, owner, "role", LocalDate.now().plusDays(1), "u");

        verify(franchiseRepository, times(1)).save(any());
        verify(auditRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.FranchiseOwnerAssignedEvent.class));
    }

    @Test
    void changeOwner_success() {
        UUID id = UUID.randomUUID();
        UUID oldOwner = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id); f.setOwnerId(oldOwner);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        UUID newOwner = UUID.randomUUID();
        service.changeOwner(id, newOwner, "role", LocalDate.now().plusDays(1), "u");

        verify(franchiseRepository, times(1)).save(any());
        verify(auditRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.FranchiseOwnershipTransferredEvent.class));
    }

}
