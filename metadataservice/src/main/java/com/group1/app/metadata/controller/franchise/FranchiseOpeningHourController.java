package com.group1.app.metadata.controller.franchise;

import com.group1.app.common.response.ApiResponse;
import com.group1.app.metadata.dto.franchise.request.UpdateOpeningHoursRequest;
import com.group1.app.metadata.dto.franchise.response.OpeningHourResponse;
import com.group1.app.metadata.service.FranchiseOpeningHourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/franchise-service/opening-hour")
@RequiredArgsConstructor
public class FranchiseOpeningHourController {

    private final FranchiseOpeningHourService service;

    @GetMapping("/{franchiseId}/opening-hours")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ApiResponse<List<OpeningHourResponse>> getOpeningHours(@PathVariable UUID franchiseId) {
        return ApiResponse.success(service.getOpeningHours(franchiseId));
    }

    @PutMapping("/{franchiseId}/opening-hours")
    @PreAuthorize("hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_OPENING_HOURS_UPDATE')")
    public ApiResponse<OpeningHourResponse> updateOpeningHours(
            @PathVariable UUID franchiseId,
            @Valid @RequestBody UpdateOpeningHoursRequest request
    ) {
        OpeningHourResponse resp = service.updateOpeningHours(franchiseId, request);
        return ApiResponse.success(resp);
    }
}
