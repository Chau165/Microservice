package com.group1.app.shift.dto.request;

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
public class ShiftCreateRequest {

    @NotNull(message = "Date cannot be null")
    LocalDate date;

    @NotNull(message = "Start time cannot be null")
    LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    LocalTime endTime;

    @NotBlank(message = "Branch ID cannot be blank")
    String branchId;
}