package com.group1.app.shift.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffAttendanceDetailsResponse {
    String shiftId;
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    String branchId;
    String shiftStatus; // OPEN, PREPARING, CLOSED
    String attendanceStatus; // PRESENT, ABSENT, LATE, EARLY_LEAVE, UNMARKED
    Integer lateMinutes;
    Integer earlyLeaveMinutes;
}