package com.group1.app.shift.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BulkMarkAttendanceRequest {

    @NotEmpty(message = "attendances must not be empty")
    @Valid
    List<AttendanceItemRequest> attendances;
}