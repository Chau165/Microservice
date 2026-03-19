package com.group1.app.shift.dto.request;

import com.group1.app.shift.enums.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceItemRequest {

    @NotBlank(message = "staffId must not be blank")
    String staffId;

    @NotNull(message = "status must not be null")
    AttendanceStatus status;

}