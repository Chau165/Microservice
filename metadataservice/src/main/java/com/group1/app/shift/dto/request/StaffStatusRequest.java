package com.group1.app.shift.dto.request;

import com.group1.app.shift.enums.StaffStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffStatusRequest {

    @NotNull(message = "Status is required")
    StaffStatus status;
}