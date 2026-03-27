package com.group1.app.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoInvalidCharactersValidator.class)
@Documented
public @interface ValidNoInvalidCharacters {
    String message() default "Must not contain double quotes (\"), dashes (-) or single quotes (')";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
