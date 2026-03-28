package com.group1.app.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class FutureShiftTimeValidator implements ConstraintValidator<ValidFutureShiftTime, Object> {

    @Override
    public void initialize(ValidFutureShiftTime annotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Class<?> clazz = value.getClass();
            var dateMethod = clazz.getMethod("getDate");
            var startTimeMethod = clazz.getMethod("getStartTime");

            Object dateObj = dateMethod.invoke(value);
            Object startTimeObj = startTimeMethod.invoke(value);

            if (dateObj == null || startTimeObj == null) {
                return true; // Let @NotNull handle null values
            }

            LocalDate date = (LocalDate) dateObj;
            LocalTime startTime = (LocalTime) startTimeObj;
            LocalDateTime shiftDateTime = LocalDateTime.of(date, startTime);

            // Check if shift start time is greater than current time (using Asia/Ho_Chi_Minh timezone)
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            return shiftDateTime.isAfter(now);
        } catch (Exception e) {
            return true;
        }
    }
}
