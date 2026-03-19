package com.group1.app.shift.repository;

import com.group1.app.shift.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findAllByShiftId(String shiftId);
    Optional<Attendance> findByShiftIdAndStaffId(String shiftId, String staffId);
}