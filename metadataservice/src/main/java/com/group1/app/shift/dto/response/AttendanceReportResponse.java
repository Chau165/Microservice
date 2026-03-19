package com.group1.app.shift.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceReportResponse {
    String staffId;
    String staffCode;
    String staffName;
    int assignedShifts;
    int presentCount;
    int absentCount;
    int totalLateMins;
    int totalEarlyMins;
    double coveragePercentage;
}