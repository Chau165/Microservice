package com.group4.shift_service.controller;

import com.group4.shift_service.dto.request.AttendanceItemRequest;
import com.group4.shift_service.dto.request.BulkMarkAttendanceRequest;
import com.group4.shift_service.dto.response.ApiResponse;
import com.group4.shift_service.dto.response.AttendanceReportResponse;
import com.group4.shift_service.dto.response.AttendanceResponse;
import com.group4.shift_service.dto.response.DashboardOverviewResponse;
import com.group4.shift_service.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
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

    @GetMapping("/dashboard")
    public ApiResponse<DashboardOverviewResponse> getDashboardOverview(@RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }
        return ApiResponse.<DashboardOverviewResponse>builder()
                .result(attendanceService.getDashboardOverview(date))
                .build();
    }

}