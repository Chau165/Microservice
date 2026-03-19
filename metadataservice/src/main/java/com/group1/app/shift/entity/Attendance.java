package com.group1.app.shift.entity;

import com.group1.app.shift.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shift_id", "staff_id"})
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "shift_id", nullable = false)
    String shiftId;

    @Column(name = "staff_id", nullable = false)
    String staffId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AttendanceStatus status;

    @Column(name = "late_minutes")
    Integer lateMinutes;

    @Column(name = "early_leave_minutes")
    Integer earlyLeaveMinutes;



    @Column(name = "marked_by", nullable = false)
    String markedBy;

    @Column(name = "marked_at")
    @CreationTimestamp
    LocalDateTime markedAt;

    @Column(name = "updated_by")
    String updatedBy;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;
}