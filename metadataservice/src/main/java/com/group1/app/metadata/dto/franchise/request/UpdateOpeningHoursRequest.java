package com.group1.app.metadata.dto.franchise.request;

import com.group1.app.metadata.enums.DayOfWeekValue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateOpeningHoursRequest {

    @NotNull(message = "Day of week must not be null")
    private DayOfWeekValue dayOfWeek;

    @NotNull(message = "Open time must not be null")
    private LocalTime openTime;

    @NotNull(message = "Close time must not be null")
    private LocalTime closeTime;

}
