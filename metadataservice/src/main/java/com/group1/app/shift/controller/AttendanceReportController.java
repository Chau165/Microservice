package com.group1.app.shift.controller;

import com.group1.app.shift.dto.response.ApiResponse;
import com.group1.app.shift.dto.response.AttendanceReportResponse;
import com.group1.app.shift.dto.response.DashboardOverviewResponse;
import com.group1.app.shift.dto.response.StaffAttendanceDetailsResponse;
import com.group1.app.shift.service.AttendanceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/shift-service/attendance-reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceReportController {

    AttendanceService attendanceService;

    @GetMapping
    public ApiResponse<List<AttendanceReportResponse>> getReport(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String branchId
    ) {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        return ApiResponse.<List<AttendanceReportResponse>>builder()
                .message("Lấy báo cáo chuyên cần thành công")
                .result(attendanceService.getAttendanceReport(targetMonth, targetYear, branchId))
                .build();
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardOverviewResponse> getDashboardOverview(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(required = false) String branchId
    ) {
        if (date == null) {
            date = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }

        return ApiResponse.<DashboardOverviewResponse>builder()
                .message("Lấy dữ liệu Dashboard thành công")
                .result(attendanceService.getDashboardOverview(date, branchId))
                .build();
    }

    // Giữ endpoint cũ, nhưng staffId giờ là "identifier" (staffId/userId/email)
    @GetMapping("/staff/{staffId}")
    public ApiResponse<List<StaffAttendanceDetailsResponse>> getStaffHistory(
            @PathVariable String staffId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate exactDate,
            @RequestParam(required = false) String branchId
    ) {
        return ApiResponse.<List<StaffAttendanceDetailsResponse>>builder()
                .message("Lấy lịch sử nhân viên thành công")
                .result(attendanceService.getStaffAttendanceHistory(staffId, month, year, exactDate, branchId))
                .build();
    }

    // Endpoint mới cho staff tự xem (khuyến nghị FE gọi endpoint này)
    @GetMapping("/staff/me")
    public ApiResponse<List<StaffAttendanceDetailsResponse>> getMyHistory(
            @RequestHeader(value = "USER", required = false) String userId,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate exactDate,
            @RequestParam(required = false) String branchId
    ) {
        String identifier = firstNonBlank(userId, userEmail);

        return ApiResponse.<List<StaffAttendanceDetailsResponse>>builder()
                .message("Lấy lịch sử nhân viên thành công")
                .result(attendanceService.getStaffAttendanceHistory(identifier, month, year, exactDate, branchId))
                .build();
    }

    private String firstNonBlank(String... values) {
        if (values == null) return "";
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return "";
    }
}
