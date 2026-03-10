package com.group4.shift_service.service;

import com.group4.shift_service.dto.request.AttendanceItemRequest;
import com.group4.shift_service.dto.request.BulkMarkAttendanceRequest;
import com.group4.shift_service.dto.response.AttendanceReportResponse;
import com.group4.shift_service.dto.response.AttendanceResponse;
import com.group4.shift_service.dto.response.DashboardOverviewResponse;
import com.group4.shift_service.dto.response.StaffAttendanceDetailsResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<AttendanceResponse> bulkMarkAttendance(String shiftId,
                                                BulkMarkAttendanceRequest request,
                                                String markedBy);

    AttendanceResponse updateAttendance(String attendanceId,
                                        AttendanceItemRequest request,
                                        String updatedBy);

    List<AttendanceResponse> getAttendanceByShift(String shiftId);

    DashboardOverviewResponse getDashboardOverview(LocalDate date);
    List<AttendanceReportResponse> getAttendanceReport(int month, int year);
    List<StaffAttendanceDetailsResponse> getStaffAttendanceHistory(String staffId, Integer month, Integer year, LocalDate exactDate);
}