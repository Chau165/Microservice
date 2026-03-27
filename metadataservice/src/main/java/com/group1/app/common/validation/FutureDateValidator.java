package com.group1.app.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureDateValidator implements ConstraintValidator<ValidFutureDate, LocalDate> {

    @Override
    public void initialize(ValidFutureDate annotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }

        // Check if date is today or in the future
        return !value.isBefore(LocalDate.now());
    }
}
