package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.franchise.request.CreateFranchiseRequest;
import com.group1.app.metadata.dto.franchise.request.UpdateFranchiseRequest;
import com.group1.app.metadata.dto.franchise.response.FranchiseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.group1.app.metadata.dto.franchise.response.FranchiseConfigurationResponse;

public interface FranchiseService {

    FranchiseResponse create(CreateFranchiseRequest request);

    List<FranchiseResponse> getAll();

    FranchiseResponse getById(UUID id);

    FranchiseResponse updateIdentity(UUID id, UpdateFranchiseRequest request, String changedBy);

    void activate(UUID id, String activatedBy);

    void deactivate(UUID id, String activatedBy);

    Page<FranchiseResponse> getAllByManager(String managerUserId, Pageable pageable);

    void suspend(UUID franchiseId, String reason, String suspendedBy);

    void assignOwner(UUID franchiseId, UUID ownerId, String role, LocalDate effectiveDate, String assignedBy);

    void changeOwner(UUID franchiseId, UUID newOwnerId, String role, LocalDate effectiveDate, String changedBy);

    FranchiseConfigurationResponse getConfiguration(UUID franchiseId);

    void assignMenuProfile(UUID franchiseId, UUID menuProfileId, String assignedBy);

    void approveSupplierList(UUID franchiseId, List<UUID> supplierIds, String approvedBy);
}
