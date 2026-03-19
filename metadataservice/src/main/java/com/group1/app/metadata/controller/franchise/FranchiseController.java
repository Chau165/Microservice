package com.group1.app.metadata.controller.franchise;

import com.group1.app.common.response.ApiResponse;
import com.group1.app.common.security.UserPrincipal;
import com.group1.app.metadata.dto.franchise.request.*;
import com.group1.app.metadata.dto.franchise.response.FranchiseResponse;
import com.group1.app.metadata.service.FranchiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/franchise-service/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;

    private String getCurrentUser(Authentication authentication) {
        return authentication.getName();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_CREATE')")
    public ApiResponse<FranchiseResponse> create(
            @Valid @RequestBody CreateFranchiseRequest body,
            Authentication authentication) {

        return ApiResponse.success(
                franchiseService.create(body)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_VIEW')")
    public ApiResponse<FranchiseResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(
                franchiseService.getById(id)
        );
    }

    @GetMapping("/{id}/configuration")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_CONFIGURATION_VIEW')")
    public ApiResponse<com.group1.app.metadata.dto.franchise.response.FranchiseConfigurationResponse> getConfiguration(
            @PathVariable UUID id) {

        return ApiResponse.success(
                franchiseService.getConfiguration(id)
        );
    }

    @PutMapping("/{id}/identity")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_UPDATE')")
    public ApiResponse<FranchiseResponse> updateIdentity(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFranchiseRequest body,
            Authentication authentication) {

        return ApiResponse.success(
                franchiseService.updateIdentity(id, body, getCurrentUser(authentication))
        );
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_ACTIVATE')")
    public ApiResponse<Void> activate(
            @PathVariable UUID id,
            Authentication authentication) {

        franchiseService.activate(id, getCurrentUser(authentication));
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_DEACTIVATE')")
    public ApiResponse<Void> deactivate(
            @PathVariable UUID id,
            Authentication authentication) {

        franchiseService.deactivate(id, getCurrentUser(authentication));
        return ApiResponse.success(null);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_VIEW_MANAGER')")
    public ApiResponse<Page<FranchiseResponse>> getMyFranchises(
            Authentication authentication,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        String managerUserId = resolveManagerUserId(authentication, jwt);

        return ApiResponse.success(
                franchiseService.getAllByManager(managerUserId, pageable)
        );
    }

    private String resolveManagerUserId(Authentication authentication, Jwt jwt) {
        String managerUserId = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            managerUserId = userPrincipal.getUserId();
        }

        if ((managerUserId == null || managerUserId.isBlank()) && jwt != null) {
            managerUserId = jwt.getClaimAsString("userId");
            if (managerUserId == null || managerUserId.isBlank()) {
                managerUserId = jwt.getSubject();
            }
        }

        if (managerUserId == null || managerUserId.isBlank()) {
            throw new AccessDeniedException("Missing manager identity");
        }

        return managerUserId;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_VIEW_ALL')")
    public ApiResponse<List<FranchiseResponse>> getAll() {
        return ApiResponse.success(
                franchiseService.getAll()
        );
    }

    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_SUSPEND')")
    public ApiResponse<String> suspendFranchise(
            @PathVariable UUID id,
            @Valid @RequestBody SuspendFranchiseRequest request,
            Authentication authentication) {

        franchiseService.suspend(
                id,
                request.reason(),
                getCurrentUser(authentication)
        );

        return ApiResponse.success("Franchise suspended successfully.");
    }

    @PostMapping("/{franchiseId}/owner")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_OWNER_ASSIGN')")
    public ApiResponse<String> assignOwner(
            @PathVariable UUID franchiseId,
            @RequestBody AssignFranchiseOwnerRequest request
    ) {

        franchiseService.assignOwner(
                franchiseId,
                request.ownerId(),
                request.role(),
                request.effectiveDate(),
                "admin"
        );

        return ApiResponse.success("Franchise owner assigned successfully.");
    }

    @PutMapping("/{franchiseId}/change-owner")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_OWNER_CHANGE')")
    public void changeOwner(
            @PathVariable UUID franchiseId,
            @RequestBody ChangeFranchiseOwnerRequest request,
            @RequestHeader("X-User") String changedBy
    ) {

        franchiseService.changeOwner(
                franchiseId,
                request.newOwnerId(),
                request.role(),
                request.effectiveDate(),
                changedBy
        );
    }

    @PostMapping("/{franchiseId}/menu-profile")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_MENU_ASSIGN')")
    public ApiResponse<String> assignMenuProfile(
            @PathVariable UUID franchiseId,
            @Valid @RequestBody AssignMenuProfileRequest request,
            Authentication authentication
    ) {

        franchiseService.assignMenuProfile(
                franchiseId,
                request.menuProfileId(),
                getCurrentUser(authentication)
        );

        return ApiResponse.success("Menu profile assigned successfully.");
    }

    @PutMapping("/{franchiseId}/suppliers/approve")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_SUPPLIER_APPROVE')")
    public ApiResponse<String> approveSupplierList(
            @PathVariable UUID franchiseId,
            @RequestBody ApproveSupplierListRequest request,
            Authentication authentication
    ) {

        franchiseService.approveSupplierList(
                franchiseId,
                request.supplierIds(),
                authentication.getName()
        );

        return ApiResponse.success("Supplier list approved successfully.");
    }
}