package com.group1.app.shift.entity;

import com.group1.app.shift.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "shift_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shift_id", "staff_id"})
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "shift_id", nullable = false)
    String shiftId;

    @Column(name = "staff_id", nullable = false)
    String staffId;

    @Column(name = "assigned_by", nullable = false)
    String assignedBy;

    @Column(name = "assigned_at")
    @CreationTimestamp
    LocalDateTime assignedAt;

    @Column(name = "date")
    LocalDate date;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "branch_id")
    String branchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    ScheduleStatus status = ScheduleStatus.SCHEDULED;
}