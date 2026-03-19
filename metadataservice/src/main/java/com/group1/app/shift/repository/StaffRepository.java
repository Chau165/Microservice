package com.group1.app.shift.repository;

import com.group1.app.shift.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffRepository extends JpaRepository<Staff, String> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Staff> findByEmail(String email);

    Optional<Staff> findByPhone(String phone);

    Optional<Staff> findByUserId(String userId);

    Optional<Staff> getStaffById(String staffId);

    Page<Staff> findAllByBranchId(String branchId, Pageable pageable);
}