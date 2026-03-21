package com.group1.app.metadata.controller.franchise;

import com.group1.app.common.response.ApiResponse;
import com.group1.app.metadata.dto.franchise.request.CreateSupplierMappingRequest;
import com.group1.app.metadata.dto.franchise.request.SupplierMappingDecisionRequest;
import com.group1.app.metadata.service.SupplierMappingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/franchise-service/{franchiseId}/supplier-requests")
@RequiredArgsConstructor
public class SupplierMappingRequestController {

    private final SupplierMappingRequestService service;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_SUPPLIER_REQUEST_CREATE')")
    public ApiResponse<Void> createRequest(
            @PathVariable UUID franchiseId,
            @RequestBody CreateSupplierMappingRequest request
    ) {

        service.requestSupplierChange(franchiseId, request);

        return ApiResponse.success(null);
    }

    @PostMapping("/decision")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_SUPPLIER_REQUEST_DECIDE')")
    public ApiResponse<Void> processDecision(
            @RequestBody SupplierMappingDecisionRequest request
    ) {

        service.processDecision(request);

        return ApiResponse.success(null);
    }
}