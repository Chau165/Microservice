package com.group5.engagement.repository;

import com.group5.engagement.entity.Redemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
}
