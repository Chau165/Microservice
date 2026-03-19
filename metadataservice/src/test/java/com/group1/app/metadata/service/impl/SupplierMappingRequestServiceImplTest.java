package com.group1.app.metadata.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.franchise.request.CreateSupplierMappingRequest;
import com.group1.app.metadata.dto.franchise.request.SupplierMappingDecisionRequest;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.SupplierMappingRequest;
import com.group1.app.metadata.entity.franchise.SupplierMappingRequestStatus;
import com.group1.app.metadata.infrastructure.SupplierClient;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.repository.franchise.SupplierMappingRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierMappingRequestServiceImplTest {

    @InjectMocks
    SupplierMappingRequestServiceImpl service;

    @Mock
    SupplierMappingRequestRepository repository;

    @Mock
    FranchiseRepository franchiseRepository;

    @Mock
    SupplierClient supplierClient;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Test
    void requestSupplierChange_franchiseNotFound() {
        UUID id = UUID.randomUUID();
        when(franchiseRepository.findById(id)).thenReturn(Optional.empty());
        CreateSupplierMappingRequest req = new CreateSupplierMappingRequest();
        req.setSupplierId(UUID.randomUUID());

        ApiException ex = assertThrows(ApiException.class, () -> service.requestSupplierChange(id, req));
        assertEquals(ErrorCode.FR_404_FRANCHISE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void requestSupplierChange_supplierNotFound() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        when(supplierClient.getSupplierById(anyString())).thenReturn(null);

        CreateSupplierMappingRequest req = new CreateSupplierMappingRequest();
        req.setSupplierId(UUID.randomUUID());

        ApiException ex = assertThrows(ApiException.class, () -> service.requestSupplierChange(id, req));
        assertEquals(ErrorCode.SUPPLIER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void requestSupplierChange_success() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise(); f.setId(id);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        JsonNode node = mock(JsonNode.class);
        when(supplierClient.getSupplierById(anyString())).thenReturn(node);
        when(repository.save(any(SupplierMappingRequest.class))).thenAnswer(i -> i.getArgument(0));

        CreateSupplierMappingRequest req = new CreateSupplierMappingRequest();
        req.setSupplierId(UUID.randomUUID());
        req.setReason("r");

        service.requestSupplierChange(id, req);

        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    @Test
    void processDecision_notFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        SupplierMappingDecisionRequest req = new SupplierMappingDecisionRequest();
        req.setRequestId(UUID.randomUUID());

        ApiException ex = assertThrows(ApiException.class, () -> service.processDecision(req));
        assertEquals(ErrorCode.INTERNAL_ERROR, ex.getErrorCode());
    }

    @Test
    void processDecision_invalidStatus() {
        SupplierMappingRequest entity = new SupplierMappingRequest();
        entity.setStatus(SupplierMappingRequestStatus.APPROVED);
        when(repository.findById(any())).thenReturn(Optional.of(entity));
        SupplierMappingDecisionRequest req = new SupplierMappingDecisionRequest();
        req.setRequestId(UUID.randomUUID());
        req.setDecision("APPROVE");

        ApiException ex = assertThrows(ApiException.class, () -> service.processDecision(req));
        assertEquals(ErrorCode.INVALID_FRANCHISE_STATUS, ex.getErrorCode());
    }

    @Test
    void processDecision_approve_success() {
        SupplierMappingRequest entity = new SupplierMappingRequest();
        entity.setStatus(SupplierMappingRequestStatus.PENDING);
        when(repository.findById(any())).thenReturn(Optional.of(entity));

        SupplierMappingDecisionRequest req = new SupplierMappingDecisionRequest();
        req.setRequestId(UUID.randomUUID());
        req.setDecision("APPROVE");
        req.setComment("ok");

        service.processDecision(req);

        assertEquals(SupplierMappingRequestStatus.APPROVED, entity.getStatus());
        verify(repository, times(1)).save(entity);
    }

    @Test
    void processDecision_reject_success() {
        SupplierMappingRequest entity = new SupplierMappingRequest();
        entity.setStatus(SupplierMappingRequestStatus.PENDING);
        when(repository.findById(any())).thenReturn(Optional.of(entity));

        SupplierMappingDecisionRequest req = new SupplierMappingDecisionRequest();
        req.setRequestId(UUID.randomUUID());
        req.setDecision("REJECT");
        req.setComment("no");

        service.processDecision(req);

        assertEquals(SupplierMappingRequestStatus.REJECTED, entity.getStatus());
        verify(repository, times(1)).save(entity);
    }
}

