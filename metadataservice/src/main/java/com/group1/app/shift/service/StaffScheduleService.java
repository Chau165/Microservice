package com.group1.app.shift.service;

import com.group1.app.shift.dto.request.StaffScheduleRequest;
import com.group1.app.shift.dto.response.StaffScheduleResponse;
import com.group1.app.shift.dto.response.StaffScheduleWithAttendanceResponse;

import java.util.List;

public interface StaffScheduleService {
    List<StaffScheduleResponse> getSchedulesByStaffId(String staffId);
    StaffScheduleResponse createSchedule(StaffScheduleRequest request);
    StaffScheduleResponse updateSchedule(String scheduleId, StaffScheduleRequest request);
    void deleteSchedule(String scheduleId);

    List<StaffScheduleWithAttendanceResponse> getSchedulesWithAttendance(String staffId);
}
