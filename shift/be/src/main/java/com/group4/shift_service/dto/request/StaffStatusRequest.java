package com.group4.shift_service.dto.request;

import com.group4.shift_service.enums.StaffStatus;
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