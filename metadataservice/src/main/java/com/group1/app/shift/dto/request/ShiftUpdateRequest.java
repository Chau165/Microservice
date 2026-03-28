package com.group1.app.shift.dto.request;

import com.group1.app.common.validation.ValidShiftTime;
import com.group1.app.common.validation.ValidFutureShiftTime;
import com.group1.app.common.validation.ValidFutureDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidShiftTime(message = "End time must be greater than start time")
@ValidFutureShiftTime(message = "Shift start time must be greater than current time")
public class ShiftUpdateRequest {

    @NotNull(message = "Date cannot be null")
    @ValidFutureDate(message = "Date must be today or in the future")
    LocalDate date;

    @NotNull(message = "Start time cannot be null")
    LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    LocalTime endTime;

    @NotBlank(message = "Branch ID cannot be blank")
    String branchId;
}