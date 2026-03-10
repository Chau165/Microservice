package com.group4.shift_service.repository;

import com.group4.shift_service.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, String> {
    List<StaffSchedule> findAllByStaffId(String staffId);
}
