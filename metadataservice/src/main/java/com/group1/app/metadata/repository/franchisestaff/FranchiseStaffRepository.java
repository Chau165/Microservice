package com.group1.app.metadata.repository.franchisestaff;

import com.group1.app.metadata.entity.franchisestaff.FranchiseStaff;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FranchiseStaffRepository extends JpaRepository<FranchiseStaff, UUID> {

    List<FranchiseStaff> findAllByFranchiseId(UUID franchiseId);

    List<FranchiseStaff> findAllByStaffId(String staffId);

    Optional<FranchiseStaff> findByStaffIdAndStatus(
            String staffId,
            FranchiseStaffStatus status
    );

    Optional<FranchiseStaff> findByStaffIdAndFranchiseIdAndStatus(
            String staffId,
            UUID franchiseId,
            FranchiseStaffStatus status
    );
}