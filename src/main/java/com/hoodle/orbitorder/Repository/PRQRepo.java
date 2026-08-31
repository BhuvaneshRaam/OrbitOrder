package com.hoodle.orbitorder.Repository;

import com.hoodle.orbitorder.DTO.PrqSummaryResponse;
import com.hoodle.orbitorder.Entity.PRQ;
import com.hoodle.orbitorder.Enum.PrStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PRQRepo extends JpaRepository <PRQ, UUID> {

    Page<PRQ> findByRequesterIdAndTenantId(UUID requesterId, UUID tenantId, Pageable pageable);
    Page<PRQ> findByTenantId(UUID tenantId, Pageable pageable);
    Page<PRQ> findByTenantIdAndDepartment(UUID tenantId, String department, Pageable pageable);

    // 2. For the Detail View (EntityGraph fetches items instantly)
    @EntityGraph(attributePaths = {"items"})
    Optional<PRQ> findByIdAndTenantId(UUID id, UUID tenantId);

    long countByRequesterIdAndTenantIdAndStatus(UUID requesterId, UUID tenantId, PrStatus status);

    long countByTenantIdAndStatus(UUID tenantId, PrStatus status);

    @Query("""
            SELECT new com.hoodle.orbitorder.DTO.PrqSummaryResponse(
                prq.id, prq.prNumber, prq.department, CAST(prq.status AS string),
                prq.totalAmount, prq.createdAt
            )
            FROM PRQ prq
            WHERE prq.requesterId = :requesterId
              AND prq.tenantId = :tenantId
            ORDER BY prq.createdAt DESC, prq.id DESC
            """)
    List<PrqSummaryResponse> findDashboardRecentPrqsByRequesterAndTenant(
            @Param("requesterId") UUID requesterId,
            @Param("tenantId") UUID tenantId,
            Pageable pageable
    );

    @Query("""
            SELECT new com.hoodle.orbitorder.DTO.PrqSummaryResponse(
                prq.id, prq.prNumber, prq.department, CAST(prq.status AS string),
                prq.totalAmount, prq.createdAt
            )
            FROM PRQ prq
            WHERE prq.tenantId = :tenantId
              AND prq.status = :status
            ORDER BY prq.createdAt ASC, prq.id ASC
            """)
    List<PrqSummaryResponse> findDashboardPrqsByTenantAndStatusOldestFirst(
            @Param("tenantId") UUID tenantId,
            @Param("status") PrStatus status,
            Pageable pageable
    );
}
