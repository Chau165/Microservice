package com.group1.app.metadata.controller.franchise;

import com.group1.app.common.response.ApiResponse;
import com.group1.app.metadata.dto.franchise.request.UpdateWarehouseMappingRequest;
import com.group1.app.metadata.dto.franchise.response.WarehouseMappingResponse;
import com.group1.app.metadata.entity.franchise.FranchiseWarehouseMapping;
import com.group1.app.metadata.service.FranchiseWarehouseMappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/franchise-service/franchise-warehouse")
@RequiredArgsConstructor
public class FranchiseWarehouseMappingController {

    private final FranchiseWarehouseMappingService warehouseMappingService;

    @PutMapping("/franchise/{franchiseId}")
    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('FRANCHISE_WAREHOUSE_UPDATE')")
    public ResponseEntity<ApiResponse<WarehouseMappingResponse>> updateWarehouseMapping(
            @PathVariable UUID franchiseId,
            @Valid @RequestBody UpdateWarehouseMappingRequest request,
            Authentication authentication) {

        WarehouseMappingResponse result = warehouseMappingService.updateWarehouseMapping(
                franchiseId,
                request.warehouseId(),
                authentication.getName()
        );

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/franchise/{franchiseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_WAREHOUSE_VIEW')")
    public ResponseEntity<ApiResponse<List<FranchiseWarehouseMapping>>> getByFranchise(
            @PathVariable UUID franchiseId) {

        List<FranchiseWarehouseMapping> result =
                warehouseMappingService.getAllByFranchiseId(franchiseId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}