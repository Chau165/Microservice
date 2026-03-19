package com.group1.app.shift.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffCreateRequest {
    @NotBlank(message = "Name is required")
    String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^(03|05|07|08|09)\\d{8}$",
            message = "Phone must be a valid Vietnamese number (10 digits, starts with 03/05/07/08/09)"
    )
    String phone;
    @NotBlank(message = "gender is required")
    String gender;

    @NotBlank(message = "Branch is required")
    String branchId;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth;
}