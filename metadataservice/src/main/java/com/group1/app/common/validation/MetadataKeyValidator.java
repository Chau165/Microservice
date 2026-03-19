package com.group1.app.common.validation;

import com.group1.app.common.config.MetadataKeyConfig;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class MetadataKeyValidator implements ConstraintValidator<ValidMetadataKey, String> {

    private MetadataKeyConfig metadataKeyConfig;
    private Pattern compiledPattern;

    public MetadataKeyValidator(MetadataKeyConfig metadataKeyConfig) {
        this.metadataKeyConfig = metadataKeyConfig;
    }

    @Override
    public void initialize(ValidMetadataKey annotation) {
        if (compiledPattern == null) {
            if (metadataKeyConfig == null) {
                metadataKeyConfig = com.group1.app.common.config.ApplicationContextProvider
                        .getBean(MetadataKeyConfig.class);
            }

            String pattern = metadataKeyConfig != null ? metadataKeyConfig.getKeyPattern() : "^[A-Z_]+$";
            compiledPattern = Pattern.compile(pattern);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Allow null values (other validators handle @NotNull if needed)
        if (value == null || value.isBlank()) {
            return true;
        }

        boolean isValid = compiledPattern.matcher(value).matches();

        if (!isValid) {
            // Customize error message to show the pattern rule
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Invalid metadata key format. Must match pattern: %s",
                            metadataKeyConfig.getKeyPattern())
            ).addConstraintViolation();
        }

        return isValid;
    }
}
