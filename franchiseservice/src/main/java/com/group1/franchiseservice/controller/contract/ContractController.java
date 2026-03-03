package com.group1.franchiseservice.controller.contract;

import com.group1.franchiseservice.common.exception.ErrorCode;
import com.group1.franchiseservice.common.response.ApiResponse;
import com.group1.franchiseservice.common.response.ResponseFactory;
import com.group1.franchiseservice.model.dto.contract.request.CreateContractRequest;
import com.group1.franchiseservice.model.dto.contract.request.RenewContractRequest;
import com.group1.franchiseservice.model.dto.contract.request.TerminateContractRequest;
import com.group1.franchiseservice.model.dto.contract.response.CreateContractResponse;
import com.group1.franchiseservice.model.dto.contract.response.RenewContractResponse;
import com.group1.franchiseservice.model.dto.contract.response.TerminateContractResponse;
import com.group1.franchiseservice.model.entity.contract.Contract;
import com.group1.franchiseservice.service.contract.ContractService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ContractController {

    private final ContractService contractService;
    private final ResponseFactory responseFactory;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateContractResponse>> create(
            HttpServletRequest request,
            @Valid @RequestBody CreateContractRequest body) {

        var result = contractService.create(body);

        return ResponseEntity.status(ErrorCode.CREATED.getStatus())
                .body(responseFactory.success(
                        ErrorCode.CREATED.getCode(),
                        "Contract created successfully",
                        result,
                        request
                ));
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<ApiResponse<RenewContractResponse>> renew(
            HttpServletRequest request,
            @PathVariable UUID id,
            @Valid @RequestBody RenewContractRequest body) {

        var result = contractService.renew(id, body);

        return ResponseEntity.ok(
                responseFactory.success(
                        ErrorCode.SUCCESS.getCode(),
                        "Contract renewed successfully",
                        result,
                        request
                ));
    }

    @PutMapping("/{id}/terminate")
    public ResponseEntity<ApiResponse<TerminateContractResponse>> terminate(
            HttpServletRequest request,
            @PathVariable UUID id,
            @Valid @RequestBody TerminateContractRequest body,
            Authentication authentication) {

        var result =
                contractService.terminate(id, body, authentication.getName());

        return ResponseEntity.ok(
                responseFactory.success(
                        ErrorCode.SUCCESS.getCode(),
                        "Contract terminated successfully",
                        result,
                        request
                ));
    }

    @GetMapping("/{id}")
    public Contract getById(@PathVariable UUID id) {
        return contractService.getById(id);
    }

    // GET toàn bộ contract
    @GetMapping
    public List<Contract> getAll() {
        return contractService.getAll();
    }
}