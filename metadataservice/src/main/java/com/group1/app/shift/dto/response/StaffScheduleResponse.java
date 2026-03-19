package com.group1.app.shift.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group1.app.shift.enums.ScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffScheduleResponse {
    String id;
    String staffId;
    String shiftId;
    String shiftName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date;
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime endTime;
    String branchId;
    ScheduleStatus status;
}
