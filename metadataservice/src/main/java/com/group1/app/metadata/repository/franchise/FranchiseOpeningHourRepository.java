package com.group1.app.metadata.repository.franchise;

import com.group1.app.metadata.enums.DayOfWeekValue;
import com.group1.app.metadata.entity.franchise.FranchiseOpeningHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FranchiseOpeningHourRepository extends JpaRepository<FranchiseOpeningHour, UUID> {

    Optional<FranchiseOpeningHour> findByFranchise_IdAndDayOfWeek(UUID franchiseId, DayOfWeekValue dayOfWeek);

    List<FranchiseOpeningHour> findByFranchise_Id(UUID franchiseId);

}
