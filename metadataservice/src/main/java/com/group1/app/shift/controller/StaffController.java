package com.group1.app.shift.controller;

import com.group1.app.common.security.UserPrincipal;
import com.group1.app.shift.dto.request.StaffCreateRequest;
import com.group1.app.shift.dto.request.StaffStatusRequest;
import com.group1.app.common.response.ApiResponse;
import com.group1.app.shift.dto.response.StaffResponse;
import com.group1.app.shift.service.StaffService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift-service/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @PreAuthorize("hasAuthority('STAFF_CREATE')")
    public ApiResponse<StaffResponse> createStaff(@RequestBody @Valid StaffCreateRequest request) {
        return ApiResponse.success(staffService.createStaff(request));
    }

    @GetMapping
    public ApiResponse<?> getAllStaff(
            Authentication authentication,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;

        String managerUserId = resolveManagerUserId(authentication, jwt);

        return ApiResponse.builder()
            .data(staffService.getAllStaffs(managerUserId, page, size))
            .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_READ')")
    public ApiResponse<StaffResponse> getStaffById(@PathVariable String id) {
        return ApiResponse.success(staffService.getStaffById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    public ApiResponse<StaffResponse> updateStaffById(
            @PathVariable String id,
            @RequestBody @Valid StaffCreateRequest request) {
        return ApiResponse.success(staffService.updateStaff(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    public ApiResponse<StaffResponse> updateStatus(
            @PathVariable String id,
            @RequestBody @Valid StaffStatusRequest request) {
        return ApiResponse.success(staffService.updateStatus(id, request));
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_DELETE')")
    public ApiResponse<Void> deleteStaffById(@PathVariable String id) {
        staffService.deleteStaff(id);
        return ApiResponse.success(null);
    }
}