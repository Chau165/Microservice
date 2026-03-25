package com.group1.app.shift.repository;

import com.group1.app.shift.entity.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, String> {

    // Lấy tất cả ca làm việc theo ngày
    List<Shift> findAllByDate(LocalDate date);

    // Lấy danh sách ca làm việc theo chi nhánh (có phân trang)
    Page<Shift> findAllByBranchId(String branchId, Pageable pageable);

    // Lấy danh sách ca làm việc theo ngày VÀ chi nhánh
    List<Shift> findAllByDateAndBranchId(LocalDate date, String branchId);
}