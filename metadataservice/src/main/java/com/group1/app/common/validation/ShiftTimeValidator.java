package com.group1.app.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ShiftTimeValidator implements ConstraintValidator<ValidShiftTime, Object> {

    @Override
    public void initialize(ValidShiftTime annotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            // Check if object has getStartTime() and getEndTime() methods
            Class<?> clazz = value.getClass();
            var startTimeMethod = clazz.getMethod("getStartTime");
            var endTimeMethod = clazz.getMethod("getEndTime");

            Object startTime = startTimeMethod.invoke(value);
            Object endTime = endTimeMethod.invoke(value);

            if (startTime == null || endTime == null) {
                return true; // Let @NotNull handle null values
            }

            // Compare times
            @SuppressWarnings("unchecked")
            Comparable<Object> start = (Comparable<Object>) startTime;
            return start.compareTo(endTime) < 0;
        } catch (Exception e) {
            return true; // If we can't validate, let other validators handle it
        }
    }
}
