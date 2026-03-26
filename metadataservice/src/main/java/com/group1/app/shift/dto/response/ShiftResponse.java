package com.group1.app.shift.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftResponse {
    String id;
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    String branchId;
    String createBy;
    LocalDateTime createAt;
    String status;
    int staffCount;
}