package com.group1.app.metadata.repository.franchise;

import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaffStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, UUID> {

    boolean existsByFranchiseCode(String franchiseCode);

    Page<Franchise> findByOwnerId(UUID ownerId, Pageable pageable);

    Page<Franchise> findDistinctByOwnerIdOrFranchiseStaffs_StaffIdAndFranchiseStaffs_Status(
            UUID ownerId,
            String staffId,
            FranchiseStaffStatus status,
            Pageable pageable
    );

        Page<Franchise> findDistinctByFranchiseStaffs_StaffIdAndFranchiseStaffs_Status(
            String staffId,
            FranchiseStaffStatus status,
            Pageable pageable
        );
}

