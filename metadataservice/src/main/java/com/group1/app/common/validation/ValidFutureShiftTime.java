package com.group1.app.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureShiftTimeValidator.class)
@Documented
public @interface ValidFutureShiftTime {
    String message() default "Shift start time must be greater than current time. Please select a time in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
