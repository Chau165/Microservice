package com.group1.app.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ShiftTimeValidator.class)
@Documented
public @interface ValidShiftTime {
    String message() default "End time must be greater than start time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
