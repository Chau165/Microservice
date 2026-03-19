package com.group1.app.shift.controller;

import com.group1.app.shift.dto.request.StaffScheduleRequest;
import com.group1.app.common.response.ApiResponse;
import com.group1.app.shift.dto.response.StaffScheduleResponse;
import com.group1.app.shift.dto.response.StaffScheduleWithAttendanceResponse;
import com.group1.app.shift.service.StaffScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift-service")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffScheduleController {

    StaffScheduleService staffScheduleService;

    @GetMapping("/staff/{staffId}/schedules")
    public ApiResponse<List<StaffScheduleResponse>> getSchedulesByStaff(@PathVariable String staffId) {
        return ApiResponse.<List<StaffScheduleResponse>>builder()
            .data(staffScheduleService.getSchedulesByStaffId(staffId))
            .build();
    }

    @GetMapping("/staff-schedules/{staffId}")
    public ApiResponse<List<StaffScheduleWithAttendanceResponse>> getSchedulesWithAttendance(@PathVariable String staffId) {
        return ApiResponse.<List<StaffScheduleWithAttendanceResponse>>builder()
            .data(staffScheduleService.getSchedulesWithAttendance(staffId))
            .build();
    }

    @PostMapping("/staff/{staffId}/schedules")
    public ApiResponse<StaffScheduleResponse> createSchedule(
            @PathVariable String staffId,
            @RequestBody @Valid StaffScheduleRequest request) {
        // ensure path staffId matches payload
        if (request.getStaffId() == null) request.setStaffId(staffId);
        return ApiResponse.<StaffScheduleResponse>builder()
            .message("Schedule created")
            .data(staffScheduleService.createSchedule(request))
            .build();
    }

    @PutMapping("/schedules/{scheduleId}")
    public ApiResponse<StaffScheduleResponse> updateSchedule(
            @PathVariable String scheduleId,
            @RequestBody @Valid StaffScheduleRequest request) {
        return ApiResponse.<StaffScheduleResponse>builder()
            .message("Schedule updated")
            .data(staffScheduleService.updateSchedule(scheduleId, request))
            .build();
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ApiResponse<Void> deleteSchedule(@PathVariable String scheduleId) {
        staffScheduleService.deleteSchedule(scheduleId);
        return ApiResponse.<Void>builder().message("Schedule deleted").build();
    }
}
