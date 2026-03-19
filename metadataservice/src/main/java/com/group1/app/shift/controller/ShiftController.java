package com.group1.app.shift.controller;

import com.group1.app.common.response.ApiResponse;
import com.group1.app.common.response.PageResponse;
import com.group1.app.common.security.UserPrincipal;
import com.group1.app.shift.dto.request.ShiftAssignmentRequest;
import com.group1.app.shift.dto.request.ShiftCreateRequest;
import com.group1.app.shift.dto.request.ShiftUpdateRequest;
import com.group1.app.shift.dto.response.ShiftResponse;
import com.group1.app.shift.dto.response.StaffResponse;
import com.group1.app.shift.service.ShiftService;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shift-service/shifts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShiftController {

    ShiftService shiftService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SHIFT_CREATE')")
    public ApiResponse<ShiftResponse> createShift(@RequestBody @Valid ShiftCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return ApiResponse.success(
                shiftService.createShift(request, userPrincipal.getUserId())
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SHIFT_READ')")
    public ApiResponse<?> getAllShifts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (date != null) {
            return ApiResponse.success(shiftService.getShiftsByDate(date));
        }

        Page<ShiftResponse> result = shiftService.getAllShifts(page, size);
        return ApiResponse.success(PageResponse.from(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SHIFT_READ')")
    public ApiResponse<ShiftResponse> getShiftById(@PathVariable String id) {
        return ApiResponse.success(shiftService.getShiftById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SHIFT_UPDATE')")
    public ApiResponse<ShiftResponse> updateShift(
            @PathVariable String id,
            @RequestBody @Valid ShiftUpdateRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return ApiResponse.success(
                shiftService.updateShift(id, request, userPrincipal.getUserId())
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SHIFT_DELETE')")
    public ApiResponse<Void> deleteShift(@PathVariable String id) {
        shiftService.deleteShift(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{shiftId}/staff")
    @PreAuthorize("hasAuthority('SHIFT_READ')")
    public ApiResponse<List<StaffResponse>> getStaffByShift(@PathVariable String shiftId) {
        return ApiResponse.success(shiftService.getStaffByShift(shiftId));
    }

    @PostMapping("/{shiftId}/assign")
    @PreAuthorize("hasAuthority('SHIFT_ASSIGN')")
    public ApiResponse<Void> assignStaffToShift(
            @PathVariable String shiftId,
            @RequestBody @Valid ShiftAssignmentRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        shiftService.assignStaffToShift(shiftId, request.getStaffId(), userPrincipal.getUserId());
        return ApiResponse.success(null);
    }
}