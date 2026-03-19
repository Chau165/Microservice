package com.group1.app.metadata.controller.contract;

import com.group1.app.common.response.ApiResponse;
import com.group1.app.common.response.PageResponse;
import com.group1.app.common.security.UserPrincipal;
import com.group1.app.metadata.dto.contract.request.CreateContractRequest;
import com.group1.app.metadata.dto.contract.request.RenewContractRequest;
import com.group1.app.metadata.dto.contract.request.TerminateContractRequest;
import com.group1.app.metadata.dto.contract.response.*;
import com.group1.app.metadata.entity.contract.ContractStatus;
import com.group1.app.metadata.service.ContractService;
import com.group1.app.common.util.ContractSortFields;
import com.group1.app.common.util.PageableMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/franchise-service/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

//    private String getCurrentUser() {
//        return "admin-test-user";
//    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('CONTRACT_CREATE')")
    public ApiResponse<CreateContractResponse> create(
            @Valid @RequestBody CreateContractRequest body) {
        return ApiResponse.success(contractService.create(body));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('CONTRACT_ACTIVATE')")
    public ApiResponse<ActivateContractResponse> activate(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {

        String actor = (user != null) ? user.getName() : "SYSTEM";
        return ApiResponse.success(contractService.activate(id, actor));
    }

    @PutMapping("/{id}/renew")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('CONTRACT_RENEW')")
    public ApiResponse<RenewContractResponse> renew(
            @PathVariable UUID id,
            @Valid @RequestBody RenewContractRequest body,
            @AuthenticationPrincipal UserPrincipal user) {

        String actor = (user != null) ? user.getName() : "SYSTEM";
        return ApiResponse.success(contractService.renew(id, body, actor));
    }

    @PutMapping("/{id}/terminate")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('CONTRACT_TERMINATE')")
    public ApiResponse<TerminateContractResponse> terminate(
            @PathVariable UUID id,
            @Valid @RequestBody TerminateContractRequest body,
            @AuthenticationPrincipal UserPrincipal user) {

        String actor = (user != null) ? user.getName() : "SYSTEM";
        return ApiResponse.success(contractService.terminate(id, body, actor));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('CONTRACT_VIEW')")
    public ApiResponse<ContractResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(contractService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('CONTRACT_VIEW')")
    public ApiResponse<PageResponse<ContractListResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = PageableMapper.createPageable(
                page, size, sort, ContractSortFields.FIELDS, "createdAt");

        var result = contractService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(result));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('CONTRACT_SEARCH')")
    public ApiResponse<PageResponse<ContractListResponse>> searchContracts(
            @RequestParam(required = false) UUID franchiseId,
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = PageableMapper.createPageable(
                page, size, sort, ContractSortFields.FIELDS, "createdAt");

        var result = contractService.searchContracts(
                franchiseId, status, startDateFrom, startDateTo, pageable);

        return ApiResponse.success(PageResponse.from(result));
    }


}
