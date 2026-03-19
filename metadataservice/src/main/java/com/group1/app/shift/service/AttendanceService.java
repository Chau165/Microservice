package com.group1.app.shift.service;

import com.group1.app.shift.dto.request.AttendanceItemRequest;
import com.group1.app.shift.dto.request.BulkMarkAttendanceRequest;
import com.group1.app.shift.dto.response.AttendanceReportResponse;
import com.group1.app.shift.dto.response.AttendanceResponse;
import com.group1.app.shift.dto.response.DashboardOverviewResponse;
import com.group1.app.shift.dto.response.StaffAttendanceDetailsResponse;

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