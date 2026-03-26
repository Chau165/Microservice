package com.group1.app.shift.controller;

import com.group1.app.shift.dto.request.AttendanceItemRequest;
import com.group1.app.shift.dto.request.BulkMarkAttendanceRequest;
import com.group1.app.shift.dto.response.ApiResponse;
import com.group1.app.shift.dto.response.AttendanceResponse;
import com.group1.app.shift.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift-service/shifts/{shiftId}/attendance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceController {

    AttendanceService attendanceService;

    @GetMapping
    public ApiResponse<List<AttendanceResponse>> getByShift(@PathVariable String shiftId) {
        return ApiResponse.<List<AttendanceResponse>>builder()
                .message("Attendance retrieved successfully")
                .result(attendanceService.getAttendanceByShift(shiftId))
                .build();
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<List<AttendanceResponse>> bulkMark(
            @PathVariable String shiftId,
            @RequestBody @Valid BulkMarkAttendanceRequest request,
            @RequestHeader(value = "USER", defaultValue = "admin_01") String markedBy) {
        return ApiResponse.<List<AttendanceResponse>>builder()
                .message("Attendance marked successfully")
                .result(attendanceService.bulkMarkAttendance(shiftId, request, markedBy))
                .build();
    }

    @PatchMapping("/{attendanceId}")
    public ApiResponse<AttendanceResponse> update(
            @PathVariable String shiftId,
            @PathVariable String attendanceId,
            @RequestBody @Valid AttendanceItemRequest request,
            @RequestHeader(value = "USER", defaultValue = "admin_01") String updatedBy) {
        return ApiResponse.<AttendanceResponse>builder()
                .message("Attendance updated successfully")
                .result(attendanceService.updateAttendance(attendanceId, request, updatedBy))
                .build();
    }
}