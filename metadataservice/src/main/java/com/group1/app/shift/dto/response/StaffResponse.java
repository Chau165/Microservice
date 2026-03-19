package com.group1.app.shift.dto.response;

import com.group1.app.shift.enums.StaffStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffResponse {
    String id;
    String name;
    String email;
    String phone;
    String branchId;
    StaffStatus status;
    LocalDate dateOfBirth;
    LocalDateTime createdAt;
    String staffCode;
    String gender;
}