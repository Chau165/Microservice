package com.group4.shift_service.dto.response;

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