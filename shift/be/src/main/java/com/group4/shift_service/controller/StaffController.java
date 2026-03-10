package com.group4.shift_service.controller;

import com.group4.shift_service.dto.request.StaffCreateRequest;
import com.group4.shift_service.dto.request.StaffStatusRequest;
import com.group4.shift_service.dto.response.ApiResponse;
import com.group4.shift_service.dto.response.StaffResponse;
import com.group4.shift_service.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift-service/staffs")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;

    @PostMapping
    public ApiResponse<StaffResponse> createStaff(@RequestBody @Valid StaffCreateRequest request) {
        return ApiResponse.<StaffResponse>builder().result(staffService.createStaff(request)).build();
    }

    @GetMapping
    public ApiResponse<Page<StaffResponse>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<Page<StaffResponse>>builder().result(staffService.getAllStaffs(page, size)).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<StaffResponse> getStaffById(@PathVariable String id) {
        return ApiResponse.<StaffResponse>builder().result(staffService.getStaffById(id)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<StaffResponse> updateStaffById(@PathVariable String id, @RequestBody @Valid StaffCreateRequest request) {
        return ApiResponse.<StaffResponse>builder().result(staffService.updateStaff(id, request)).build();
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