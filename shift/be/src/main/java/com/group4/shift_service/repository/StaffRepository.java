package com.group4.shift_service.repository;

import com.group4.shift_service.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, String> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Staff> findByEmail(String email);

    Optional<Staff> findByPhone(String phone);
}