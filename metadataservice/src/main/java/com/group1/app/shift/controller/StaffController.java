package com.group1.app.shift.controller;

import com.group1.app.shift.dto.response.ApiResponse;
import com.group1.app.common.security.UserPrincipal;
import com.group1.app.shift.dto.request.StaffCreateRequest;
import com.group1.app.shift.dto.request.StaffStatusRequest;
import com.group1.app.shift.dto.response.StaffResponse;
import com.group1.app.shift.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift-service/staffs")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;

    @PostMapping
    public ApiResponse<StaffResponse> createStaff(
            @RequestBody @Valid StaffCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            throw new AccessDeniedException("Missing authentication");
        }

        String managerUserId = userPrincipal.getUserId();

        // Set managerUserId from UserPrincipal (injected by API Gateway headers)
        request.setManagerUserId(managerUserId);

        return ApiResponse.<StaffResponse>builder()
                .result(staffService.createStaff(request))
                .build();
    }


    @GetMapping
    public ApiResponse<Page<StaffResponse>> getAllStaff(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;

        if (userPrincipal == null) {
            throw new AccessDeniedException("Missing authentication");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        String managerUserId = userPrincipal.getUserId();

        return ApiResponse.<Page<StaffResponse>>builder()
            .result(staffService.getAllStaffs(managerUserId, page, size))
            .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<StaffResponse> getStaffById(@PathVariable String id) {
        return ApiResponse.<StaffResponse>builder().result(staffService.getStaffById(id)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<StaffResponse> updateStaffById(
            @PathVariable String id,
            @RequestBody @Valid StaffCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            throw new AccessDeniedException("Missing authentication");
        }

        String managerUserId = userPrincipal.getUserId();
        request.setManagerUserId(managerUserId);

        return ApiResponse.<StaffResponse>builder()
                .result(staffService.updateStaff(id, request))
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<StaffResponse> updateStatus(
            @PathVariable String id,
            @RequestBody @Valid StaffStatusRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .message("Staff status updated successfully")
                .result(staffService.updateStatus(id, request))
                .build();
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStaffById(@PathVariable String id) {
        staffService.deleteStaff(id);
        return ApiResponse.<Void>builder().message("Staff deleted").build();
    }
}