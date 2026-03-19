package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.contract.request.CreateContractRequest;
import com.group1.app.metadata.dto.contract.request.RenewContractRequest;
import com.group1.app.metadata.dto.contract.request.TerminateContractRequest;
import com.group1.app.metadata.entity.contract.Contract;
import com.group1.app.metadata.entity.contract.ContractStatus;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.mapper.contract.ContractMapper;
import com.group1.app.metadata.repository.contract.ContractAuditRepository;
import com.group1.app.metadata.repository.contract.ContractRepository;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.service.EffectiveConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @InjectMocks
    ContractServiceImpl service;

    @Mock
    ContractRepository contractRepository;

    @Mock
    ContractAuditRepository auditRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    ContractMapper contractMapper;

    @Mock
    EffectiveConfigService effectiveConfigService;

    @Mock
    FranchiseRepository franchiseRepository;

    @Mock
    com.group1.app.common.util.MetadataHelper metadataHelper;

    @Test
    void create_invalidDates() {
        CreateContractRequest req = new CreateContractRequest("C1", UUID.randomUUID(), LocalDate.now().plusDays(10), LocalDate.now(), new BigDecimal("10"));
        ApiException ex = assertThrows(ApiException.class, () -> service.create(req));
        assertEquals(ErrorCode.CT_003_INVALID_CONTRACT_DATE, ex.getErrorCode());
    }

    @Test
    void create_invalidRoyalty() {
        UUID franchiseId = UUID.randomUUID();
        CreateContractRequest req = new CreateContractRequest("C1", franchiseId, LocalDate.now(), LocalDate.now().plusDays(10), new BigDecimal("200"));
        Franchise franchise = new Franchise(); franchise.setId(franchiseId); franchise.setRegion("REGION1");
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(franchise));
        when(metadataHelper.getInt(eq("CONTRACT_DURATION_MAX_YEARS"), any(), anyInt())).thenReturn(10);
        when(metadataHelper.getDecimal(eq("CONTRACT_ROYALTY_MAX"), any(), any())).thenReturn(new BigDecimal("100"));
        ApiException ex = assertThrows(ApiException.class, () -> service.create(req));
        assertEquals(ErrorCode.CT_005_INVALID_ROYALTY_RATE, ex.getErrorCode());
    }

    @Test
    void create_duplicateContractNumber() {
        UUID franchiseId = UUID.randomUUID();
        CreateContractRequest req = new CreateContractRequest("C1", franchiseId, LocalDate.now(), LocalDate.now().plusDays(10), new BigDecimal("10"));
        Franchise franchise = new Franchise(); franchise.setId(franchiseId); franchise.setRegion("REGION1");
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(franchise));
        when(metadataHelper.getInt(eq("CONTRACT_DURATION_MAX_YEARS"), any(), anyInt())).thenReturn(10);
        when(metadataHelper.getDecimal(eq("CONTRACT_ROYALTY_MAX"), any(), any())).thenReturn(new BigDecimal("100"));
        doReturn(true).when(contractRepository).existsByContractNumber(any());
        ApiException ex = assertThrows(ApiException.class, () -> service.create(req));
        assertEquals(ErrorCode.CT_004_DUPLICATE_CONTRACT_NUMBER, ex.getErrorCode());
    }

    @Test
    void create_success() {
        UUID franchiseId = UUID.randomUUID();
        CreateContractRequest req = new CreateContractRequest("C1", franchiseId, LocalDate.now(), LocalDate.now().plusDays(10), new BigDecimal("10"));
        Franchise franchise = new Franchise(); franchise.setId(franchiseId); franchise.setRegion("REGION1");
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(franchise));
        when(metadataHelper.getInt(eq("CONTRACT_DURATION_MAX_YEARS"), any(), anyInt())).thenReturn(10);
        when(metadataHelper.getDecimal(eq("CONTRACT_ROYALTY_MAX"), any(), any())).thenReturn(new BigDecimal("100"));
        when(metadataHelper.getBoolean(eq("CONTRACT_AUTO_ORDER_DEFAULT"), any(), anyBoolean())).thenReturn(true);
        doReturn(false).when(contractRepository).existsOverlappingActiveContract(any(), any(), any());
        Contract ent = new Contract(); ent.setContractNumber("C1");
        when(contractMapper.toEntity(any())).thenReturn(ent);
        when(contractRepository.save(any())).thenAnswer(i -> { Contract c = i.getArgument(0); c.setId(UUID.randomUUID()); return c; });
        when(contractMapper.toCreateResponse(any())).thenAnswer(i -> new com.group1.app.metadata.dto.contract.response.CreateContractResponse(UUID.randomUUID(), "C1", "DRAFT"));

        var resp = service.create(req);
        assertNotNull(resp);
    }

    @Test
    void create_overlappingActiveContract() {
        UUID franchiseId = UUID.randomUUID();
        CreateContractRequest req = new CreateContractRequest("C1", franchiseId, LocalDate.now(), LocalDate.now().plusDays(5), new BigDecimal("10"));
        Franchise franchise = new Franchise(); franchise.setId(franchiseId); franchise.setRegion("REGION1");
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(franchise));
        when(metadataHelper.getInt(eq("CONTRACT_DURATION_MAX_YEARS"), any(), anyInt())).thenReturn(10);
        when(metadataHelper.getDecimal(eq("CONTRACT_ROYALTY_MAX"), any(), any())).thenReturn(new BigDecimal("100"));
        doReturn(true).when(contractRepository).existsOverlappingActiveContract(eq(franchiseId), any(), any());

        ApiException ex = assertThrows(ApiException.class, () -> service.create(req));
        assertEquals(ErrorCode.CT_007_CONTRACT_DATE_OVERLAP, ex.getErrorCode());
    }

    @Test
    void activate_notFound() {
        UUID id = UUID.randomUUID();
        when(contractRepository.findById(id)).thenReturn(Optional.empty());
        ApiException ex = assertThrows(ApiException.class, () -> service.activate(id, "u"));
        assertEquals(ErrorCode.CT_001_CONTRACT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void activate_invalidStatus() {
        UUID id = UUID.randomUUID();
        Contract c = new Contract(); c.setStatus(ContractStatus.ACTIVE);
        when(contractRepository.findById(id)).thenReturn(Optional.of(c));
        ApiException ex = assertThrows(ApiException.class, () -> service.activate(id, "u"));
        assertEquals(ErrorCode.CT_006_INVALID_CONTRACT_STATUS, ex.getErrorCode());
    }

    @Test
    void activate_activeContractExists() {
        UUID id = UUID.randomUUID();
        Contract c = new Contract(); c.setStatus(ContractStatus.DRAFT);
        Franchise f = new Franchise(); f.setId(UUID.randomUUID()); f.setRegion("REGION1"); c.setFranchise(f);
        when(contractRepository.findById(id)).thenReturn(Optional.of(c));
        when(metadataHelper.getInt(eq("CONTRACT_ACTIVE_LIMIT_PER_FRANCHISE"), any(), anyInt())).thenReturn(1);
        when(contractRepository.countByFranchiseIdAndStatus(eq(f.getId()), eq(ContractStatus.ACTIVE))).thenReturn(1L);

        ApiException ex = assertThrows(ApiException.class, () -> service.activate(id, "u"));
        assertEquals(ErrorCode.CT_002_ACTIVE_CONTRACT_EXISTS, ex.getErrorCode());
    }

    @Test
    void activate_success() {
        UUID id = UUID.randomUUID();
        Contract c = new Contract(); c.setStatus(ContractStatus.DRAFT);
        Franchise f = new Franchise(); f.setId(UUID.randomUUID()); f.setRegion("REGION1");
        c.setFranchise(f);
        when(contractRepository.findById(id)).thenReturn(Optional.of(c));
        when(metadataHelper.getInt(eq("CONTRACT_ACTIVE_LIMIT_PER_FRANCHISE"), any(), anyInt())).thenReturn(1);
        when(contractRepository.countByFranchiseIdAndStatus(eq(f.getId()), eq(ContractStatus.ACTIVE))).thenReturn(0L);

        var resp = service.activate(id, "u");
        assertEquals(ContractStatus.ACTIVE.name(), resp.status());
        verify(auditRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.contract.ContractActivatedEvent.class));
    }

    @Test
    void renew_success() {
        UUID id = UUID.randomUUID();
        Contract c = new Contract(); c.setStatus(ContractStatus.ACTIVE); c.setEndDate(LocalDate.now().plusDays(1));
        Franchise f = new Franchise(); f.setId(UUID.randomUUID()); f.setRegion("REGION1"); c.setFranchise(f);
        when(contractRepository.findById(id)).thenReturn(Optional.of(c));
        when(metadataHelper.getInt(eq("CONTRACT_RENEW_MAX_YEARS"), any(), anyInt())).thenReturn(3);

        RenewContractRequest req = new RenewContractRequest(LocalDate.now().plusDays(10));
        when(contractMapper.toRenewResponse(any())).thenReturn(new com.group1.app.metadata.dto.contract.response.RenewContractResponse(id, req.newEndDate(), ContractStatus.ACTIVE.name(), LocalDateTime.now(), "user"));
        var resp = service.renew(id, req, "u");
        assertNotNull(resp);
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.contract.ContractRenewedEvent.class));
    }

    @Test
    void renew_invalidDate() {
        UUID id = UUID.randomUUID();
        Contract c = new Contract(); c.setStatus(ContractStatus.ACTIVE); c.setEndDate(LocalDate.now().plusDays(5));
        Franchise f = new Franchise(); f.setId(UUID.randomUUID()); c.setFranchise(f);
        when(contractRepository.findById(id)).thenReturn(Optional.of(c));

        RenewContractRequest req = new RenewContractRequest(LocalDate.now().plusDays(1));
        ApiException ex = assertThrows(ApiException.class, () -> service.renew(id, req, "u"));
        assertEquals(ErrorCode.CT_003_INVALID_CONTRACT_DATE, ex.getErrorCode());
    }

    @Test
    void terminate_success() {
        UUID id = UUID.randomUUID();
        Contract c = new Contract(); c.setStatus(ContractStatus.ACTIVE);
        Franchise f = new Franchise(); f.setId(UUID.randomUUID()); c.setFranchise(f);
        when(contractRepository.findById(id)).thenReturn(Optional.of(c));
        when(contractMapper.toTerminateResponse(any())).thenReturn(new com.group1.app.metadata.dto.contract.response.TerminateContractResponse(id, ContractStatus.TERMINATED.name(), LocalDateTime.now(), "user", "reason"));

        TerminateContractRequest req = new TerminateContractRequest("reason");
        var resp = service.terminate(id, req, "u");
        assertNotNull(resp);
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.contract.ContractTerminatedEvent.class));
    }

    @Test
    void terminate_invalidStatus() {
        UUID id = UUID.randomUUID();
        Contract c = new Contract(); c.setStatus(ContractStatus.DRAFT);
        when(contractRepository.findById(id)).thenReturn(Optional.of(c));

        TerminateContractRequest req = new TerminateContractRequest("reason");
        ApiException ex = assertThrows(ApiException.class, () -> service.terminate(id, req, "u"));
        assertEquals(ErrorCode.CT_006_INVALID_CONTRACT_STATUS, ex.getErrorCode());
    }

    @Test
    void getById_success() {
        UUID id = UUID.randomUUID();
        Contract c = new Contract(); c.setContractNumber("CN"); c.setStatus(ContractStatus.ACTIVE);
        c.setStartDate(LocalDate.now()); c.setEndDate(LocalDate.now().plusDays(1)); c.setRoyaltyRate(new BigDecimal("5"));
        Franchise f = new Franchise(); f.setId(UUID.randomUUID()); c.setFranchise(f);
        when(contractRepository.findById(id)).thenReturn(Optional.of(c));
        com.group1.app.metadata.dto.contract.response.ContractResponse mockResp = 
            new com.group1.app.metadata.dto.contract.response.ContractResponse(
                id, "CN", f.getId(), "FC", ContractStatus.ACTIVE.name(), 
                LocalDate.now(), LocalDate.now().plusDays(1), LocalDateTime.now(), "user",
                LocalDateTime.now(), "user", null, null, null, null,
                new BigDecimal("5"), true, null
            );
        when(contractMapper.toDetailResponse(any())).thenReturn(mockResp);

        var resp = service.getById(id);
        assertEquals("CN", resp.contractNumber());
    }

    @Test
    void mapToContractListResponse_expired() {
        Contract c = new Contract(); c.setStatus(ContractStatus.ACTIVE); c.setEndDate(LocalDate.now().minusDays(1));
        c.setId(UUID.randomUUID()); c.setContractNumber("CN"); Franchise f = new Franchise(); f.setId(UUID.randomUUID()); c.setFranchise(f);
        when(contractMapper.toListResponse(any())).thenReturn(new com.group1.app.metadata.dto.contract.response.ContractListResponse(
                c.getId(), "CN", f.getId(), "FC", ContractStatus.ACTIVE.name(), LocalDate.now(), LocalDate.now().minusDays(1), 
                LocalDateTime.now(), "u", new BigDecimal("5"), true, LocalDateTime.now(), "u", 
                null, null, null, null
        ));
        when(contractRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(c)));

        var dto = service.getAll(PageRequest.of(0,1));
        assertNotNull(dto);
    }

    @Test
    void searchContracts_invalidDateRange() {
        ApiException ex = assertThrows(ApiException.class, () -> service.searchContracts(null, null, LocalDate.now().plusDays(5), LocalDate.now(), PageRequest.of(0,10)));
        assertEquals(ErrorCode.CT_003_INVALID_CONTRACT_DATE, ex.getErrorCode());
    }

    @Test
    void searchContracts_validateFranchiseExists() {
        UUID fid = UUID.randomUUID();
        doReturn(false).when(franchiseRepository).existsById(fid);
        ApiException ex = assertThrows(ApiException.class, () -> service.searchContracts(fid, null, null, null, PageRequest.of(0,10)));
        assertEquals(ErrorCode.FR_404_FRANCHISE_NOT_FOUND, ex.getErrorCode());
    }
}
