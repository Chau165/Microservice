package com.group1.app.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoInvalidCharactersValidator implements ConstraintValidator<ValidNoInvalidCharacters, String> {

    @Override
    public void initialize(ValidNoInvalidCharacters annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull or @NotBlank handle null values
        }

        // Check for invalid characters: quotes, dashes, etc.
        return !value.contains("\"") && !value.contains("-") && !value.contains("'");
    }
}
