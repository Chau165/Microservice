package com.group1.app.shift.repository;

import com.group1.app.shift.entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, String> {
    List<ShiftAssignment> findAllByShiftId(String shiftId);
    boolean existsByShiftIdAndStaffId(String shiftId, String staffId);
    List<ShiftAssignment> findAllByStaffId(String staffId);
    Optional<ShiftAssignment> findByShiftIdAndStaffId(String shiftId, String staffId);
}