package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.franchise.request.CreateSupplierMappingRequest;
import com.group1.app.metadata.dto.franchise.request.SupplierMappingDecisionRequest;

import java.util.UUID;

public interface SupplierMappingRequestService {
    void requestSupplierChange(UUID franchiseId, CreateSupplierMappingRequest request);
    void processDecision(SupplierMappingDecisionRequest request);
}
