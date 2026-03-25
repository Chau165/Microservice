package com.group1.app.shift.entity;

import com.group1.app.shift.enums.StaffStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staffs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // LƯU TRỮ MÃ NHÂN VIÊN VÀO DB
    @Column(name = "staff_code", unique = true)
    String staffCode;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, unique = true)
    String email;

    @Column(name = "branch_id")
    String branchId;

    @Column(name = "user_id")
    String userId;

    @Column(name = "manager_user_id")
    String managerUserId;

    @Column(unique = true)
    String phone;


    @Column(name = "gender")
    String gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    StaffStatus status = StaffStatus.ACTIVE;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}