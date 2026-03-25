package com.group1.app.metadata.repository.contract;

import com.group1.app.metadata.entity.contract.Contract;
import com.group1.app.metadata.entity.contract.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    boolean existsByContractNumber(String contractNumber);

    @Query("""
        SELECT COUNT(c) > 0 FROM Contract c
        WHERE c.franchise.id = :franchiseId
        AND c.status = 'ACTIVE'
        AND (c.startDate <= :endDate AND c.endDate >= :startDate)
    """)
    boolean existsOverlappingActiveContract(UUID franchiseId, LocalDate startDate, LocalDate endDate);

    long countByFranchiseIdAndStatus(UUID franchiseId, ContractStatus status);

    boolean existsByFranchiseIdAndStatus(UUID franchiseId, ContractStatus status);

//    boolean existsByFranchiseCodeAndStatus(String franchiseCode, ContractStatus status);

    @Query(value = """
        SELECT c FROM Contract c
        JOIN FETCH c.franchise f
        WHERE (:franchiseId IS NULL OR f.id = :franchiseId)
        AND (:status IS NULL OR c.status = :status)
        AND (:startDateFrom IS NULL OR c.startDate >= :startDateFrom)
        AND (:startDateTo IS NULL OR c.startDate <= :startDateTo)
        """,
            countQuery = """
        SELECT COUNT(c) FROM Contract c
        WHERE (:franchiseId IS NULL OR c.franchise.id = :franchiseId)
        AND (:status IS NULL OR c.status = :status)
        AND (:startDateFrom IS NULL OR c.startDate >= :startDateFrom)
        AND (:startDateTo IS NULL OR c.startDate <= :startDateTo)
        """)
    Page<Contract> searchContracts(
            @Param("franchiseId") UUID franchiseId,
            @Param("status") ContractStatus status,
            @Param("startDateFrom") LocalDate startDateFrom,
            @Param("startDateTo") LocalDate startDateTo,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "franchise")
    Page<Contract> findAll(Pageable pageable);

    @Query(value = """
        SELECT c FROM Contract c
        LEFT JOIN c.franchise f
        WHERE (COALESCE(:contractNumber, '') = '' OR LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :contractNumber, '%')))
        AND (:status IS NULL OR c.status = :status)
        AND (COALESCE(:franchiseCode, '') = '' OR LOWER(f.franchiseCode) LIKE LOWER(CONCAT('%', :franchiseCode, '%')))
        AND (COALESCE(:createdBy, '') = '' OR LOWER(c.createdBy) LIKE LOWER(CONCAT('%', :createdBy, '%')))
        AND (COALESCE(:activatedBy, '') = '' OR LOWER(c.activatedBy) LIKE LOWER(CONCAT('%', :activatedBy, '%')))
        AND (COALESCE(:renewedBy, '') = '' OR LOWER(c.renewedBy) LIKE LOWER(CONCAT('%', :renewedBy, '%')))
        AND (COALESCE(:terminatedBy, '') = '' OR LOWER(c.terminatedBy) LIKE LOWER(CONCAT('%', :terminatedBy, '%')))
    """,
            countQuery = """
        SELECT COUNT(c) FROM Contract c
        LEFT JOIN c.franchise f
        WHERE (COALESCE(:contractNumber, '') = '' OR LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :contractNumber, '%')))
        AND (:status IS NULL OR c.status = :status)
        AND (COALESCE(:franchiseCode, '') = '' OR LOWER(f.franchiseCode) LIKE LOWER(CONCAT('%', :franchiseCode, '%')))
        AND (COALESCE(:createdBy, '') = '' OR LOWER(c.createdBy) LIKE LOWER(CONCAT('%', :createdBy, '%')))
        AND (COALESCE(:activatedBy, '') = '' OR LOWER(c.activatedBy) LIKE LOWER(CONCAT('%', :activatedBy, '%')))
        AND (COALESCE(:renewedBy, '') = '' OR LOWER(c.renewedBy) LIKE LOWER(CONCAT('%', :renewedBy, '%')))
        AND (COALESCE(:terminatedBy, '') = '' OR LOWER(c.terminatedBy) LIKE LOWER(CONCAT('%', :terminatedBy, '%')))
    """)
    Page<Contract> advancedSearch(
            @Param("contractNumber") String contractNumber,
            @Param("status") ContractStatus status,
            @Param("franchiseCode") String franchiseCode,
            @Param("createdBy") String createdBy,
            @Param("activatedBy") String activatedBy,
            @Param("renewedBy") String renewedBy,
            @Param("terminatedBy") String terminatedBy,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(c) > 0 FROM Contract c
            WHERE c.franchise.id = :franchiseId
            AND c.id <> :contractId
            AND c.status = 'ACTIVE'
            AND (
                (c.startDate <= :endDate AND c.endDate >= :startDate)
            )
        """)
    boolean existsOverlappingActiveContractExcludeSelf(
            UUID franchiseId,
            LocalDate startDate,
            LocalDate endDate,
            UUID contractId
    );

}
