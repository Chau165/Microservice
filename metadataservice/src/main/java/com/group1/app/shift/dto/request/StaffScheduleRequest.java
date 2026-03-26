package com.group1.app.shift.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffScheduleRequest {
    @NotBlank(message = "staffId is required")
    String staffId;

    @NotBlank(message = "shiftName is required")
    String shiftName;

    @NotNull(message = "date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date;

    @NotNull(message = "startTime is required")
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime startTime;

    @NotNull(message = "endTime is required")
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime endTime;

    // optional fields to support assignment-based schedules
    String branchId;
    String shiftId;
}
