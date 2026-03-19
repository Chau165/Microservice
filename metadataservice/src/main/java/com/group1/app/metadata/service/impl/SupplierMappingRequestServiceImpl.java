package com.group1.app.metadata.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.franchise.request.CreateSupplierMappingRequest;
import com.group1.app.metadata.dto.franchise.request.SupplierMappingDecisionRequest;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.SupplierMappingRequest;
import com.group1.app.metadata.entity.franchise.SupplierMappingRequestStatus;
import com.group1.app.metadata.event.franchise.SupplierMappingChangeRequestedEvent;
import com.group1.app.metadata.infrastructure.SupplierClient;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.repository.franchise.SupplierMappingRequestRepository;
import com.group1.app.metadata.service.SupplierMappingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierMappingRequestServiceImpl implements SupplierMappingRequestService {

    private final SupplierMappingRequestRepository repository;
    private final FranchiseRepository franchiseRepository;
    private final SupplierClient supplierClient;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void requestSupplierChange(UUID franchiseId, CreateSupplierMappingRequest request) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        JsonNode supplier = supplierClient.getSupplierById(request.getSupplierId().toString());

        if (supplier == null) {
            throw new ApiException(ErrorCode.SUPPLIER_NOT_FOUND);
        }

        SupplierMappingRequest entity =
                SupplierMappingRequest.builder()
                        .franchiseId(franchise.getId())
                        .supplierId(request.getSupplierId())
                        .reason(request.getReason())
                        .status(SupplierMappingRequestStatus.PENDING)
                        .build();

        repository.save(entity);

        eventPublisher.publishEvent(
                SupplierMappingChangeRequestedEvent.builder()
                        .requestId(entity.getId())
                        .franchiseId(franchiseId)
                        .supplierId(request.getSupplierId())
                        .reason(request.getReason())
                        .build()
        );
    }

    @Override
    public void processDecision(SupplierMappingDecisionRequest request) {
        SupplierMappingRequest entity =
                repository.findById(request.getRequestId())
                        .orElseThrow(() -> new ApiException(ErrorCode.INTERNAL_ERROR));

        if (entity.getStatus() != SupplierMappingRequestStatus.PENDING) {
            throw new ApiException(ErrorCode.INVALID_FRANCHISE_STATUS);
        }

        if ("APPROVE".equalsIgnoreCase(request.getDecision())) {

            entity.setStatus(SupplierMappingRequestStatus.APPROVED);

        } else {

            entity.setStatus(SupplierMappingRequestStatus.REJECTED);

        }

        entity.setComment(request.getComment());

        repository.save(entity);
    }
}
