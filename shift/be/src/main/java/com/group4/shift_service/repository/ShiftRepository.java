package com.group4.shift_service.repository;

import com.group4.shift_service.entity.Shift;
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