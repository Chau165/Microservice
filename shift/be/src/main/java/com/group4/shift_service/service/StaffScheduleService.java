package com.group4.shift_service.service;

import com.group4.shift_service.dto.request.StaffScheduleRequest;
import com.group4.shift_service.dto.response.StaffScheduleResponse;
import com.group4.shift_service.dto.response.StaffScheduleWithAttendanceResponse;

import java.util.List;

public interface StaffScheduleService {
    List<StaffScheduleResponse> getSchedulesByStaffId(String staffId);
    StaffScheduleResponse createSchedule(StaffScheduleRequest request);
    StaffScheduleResponse updateSchedule(String scheduleId, StaffScheduleRequest request);
    void deleteSchedule(String scheduleId);

    List<StaffScheduleWithAttendanceResponse> getSchedulesWithAttendance(String staffId);
}
