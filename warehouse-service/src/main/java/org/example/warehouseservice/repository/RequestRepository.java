package org.example.warehouseservice.repository;

import org.example.warehouseservice.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE " +
            "(:type IS NULL OR r.requestType = :type) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:fromDate IS NULL OR CAST(r.createdDate AS date) >= CAST(:fromDate AS date)) AND " +
            "(:toDate IS NULL OR CAST(r.createdDate AS date) <= CAST(:toDate AS date))")
    Page<Request> findByFilters(@Param("type") String type,
                                @Param("status") String status,
                                @Param("fromDate") String fromDate,
                                @Param("toDate") String toDate,
                                Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = :status WHERE r.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") String status);
}