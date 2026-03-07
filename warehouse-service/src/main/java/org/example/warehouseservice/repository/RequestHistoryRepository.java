package org.example.warehouseservice.repository;

import org.example.warehouseservice.entity.RequestHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {


    @Query("SELECT h FROM RequestHistory h WHERE " +
            "(:actionType IS NULL OR h.request.requestType = :actionType) AND " +
            "(:ingredientName IS NULL OR LOWER(h.item.name) LIKE LOWER(CONCAT('%', :ingredientName, '%'))) AND " +
            "(:fromDate IS NULL OR CAST(h.completedDate AS date) >= CAST(:fromDate AS date)) AND " +
            "(:toDate IS NULL OR CAST(h.completedDate AS date) <= CAST(:toDate AS date))")
    Page<RequestHistory> findHistoryByFilters(@Param("actionType") String actionType,
                                              @Param("ingredientName") String ingredientName,
                                              @Param("fromDate") String fromDate,
                                              @Param("toDate") String toDate,
                                              Pageable pageable);
}