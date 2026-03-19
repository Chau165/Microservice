package com.group1.app.shift.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "staff_schedules")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "staff_id", nullable = false)
    String staffId;

    @Column(name = "shift_name")
    String shiftName;

    @Column(name = "date")
    LocalDate date;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    com.group1.app.shift.enums.ScheduleStatus status = com.group1.app.shift.enums.ScheduleStatus.SCHEDULED;
}
