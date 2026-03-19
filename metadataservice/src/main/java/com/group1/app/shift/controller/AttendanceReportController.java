package com.group1.app.shift.controller;

import com.group1.app.common.response.ApiResponse;
import com.group1.app.shift.dto.response.AttendanceReportResponse;
import com.group1.app.shift.dto.response.DashboardOverviewResponse;
import com.group1.app.shift.service.AttendanceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/shift-service/attendance-reports") // Đường dẫn gốc
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceReportController {

    AttendanceService attendanceService;

    // 1. API CHO TRANG ATTENDANCE COVERAGE REPORT
    // Phản hồi tại: GET http://localhost:8081/attendance-reports
    @GetMapping
    public ApiResponse<List<AttendanceReportResponse>> getReport(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        LocalDate now = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        return ApiResponse.<List<AttendanceReportResponse>>builder()
            .message("Lấy báo cáo chuyên cần thành công")
            .data(attendanceService.getAttendanceReport(targetMonth, targetYear))
            .build();
    }

    // 2. API CHO TRANG DASHBOARD
    // Phản hồi tại: GET http://localhost:8081/attendance-reports/dashboard?date=2026-03-09
    @GetMapping("/dashboard")
    public ApiResponse<DashboardOverviewResponse> getDashboardOverview(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Nếu Frontend không gửi ngày lên, tự động lấy ngày hôm nay chuẩn giờ VN
        if (date == null) {
            date = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }

        return ApiResponse.<DashboardOverviewResponse>builder()
            .message("Lấy dữ liệu Dashboard thành công")
            .data(attendanceService.getDashboardOverview(date))
            .build();
    }

    @GetMapping("/staff/{staffId}")
    public ApiResponse<List<com.group1.app.shift.dto.response.StaffAttendanceDetailsResponse>> getStaffHistory(
            @org.springframework.web.bind.annotation.PathVariable String staffId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate exactDate) {

        return ApiResponse.<List<com.group1.app.shift.dto.response.StaffAttendanceDetailsResponse>>builder()
            .message("Lấy lịch sử nhân viên thành công")
            .data(attendanceService.getStaffAttendanceHistory(staffId, month, year, exactDate))
            .build();
    }
}