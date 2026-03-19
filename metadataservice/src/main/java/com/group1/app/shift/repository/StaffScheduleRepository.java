package com.group1.app.shift.repository;

import com.group1.app.shift.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, String> {
    List<StaffSchedule> findAllByStaffId(String staffId);
}
