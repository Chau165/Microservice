package com.group1.app.shift.repository;

import com.group1.app.shift.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, String> {
    List<Shift> findAllByDate(LocalDate date);
    List<Shift> findAllByBranchId(String branchId);
    List<Shift> findAllByDateAndBranchId(LocalDate date, String branchId);
}