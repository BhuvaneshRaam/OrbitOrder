package com.hoodle.orbitorder.Repository;

import com.hoodle.orbitorder.DTO.POSummaryResponse;
import com.hoodle.orbitorder.Entity.PurchaseOrder;
import com.hoodle.orbitorder.Enum.PoStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PORepo extends JpaRepository<PurchaseOrder, UUID> {

    boolean existsByOriginalPrqId(UUID prqId);

    Optional<PurchaseOrder> findByIdAndTenantId(UUID id, UUID tenantId);

    @EntityGraph(attributePaths = "originalPrq")
    Page<PurchaseOrder> findAllByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(po.totalAmount), 0)
            FROM PurchaseOrder po
            WHERE po.tenantId = :tenantId
              AND po.createdAt >= :startOfMonth
              AND po.createdAt < :startOfNextMonth
              AND po.status IN :statuses
            """)
    BigDecimal calculateDashboardMonthlySpendByTenant(
            @Param("tenantId") UUID tenantId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("startOfNextMonth") LocalDateTime startOfNextMonth,
            @Param("statuses") List<PoStatus> statuses
    );

    @Query("""
            SELECT new com.hoodle.orbitorder.DTO.POSummaryResponse(
                po.id, po.poNumber, po.vendorName, CAST(po.status AS string),
                po.totalAmount, prq.prNumber, po.createdAt
            )
            FROM PurchaseOrder po
            JOIN po.originalPrq prq
            WHERE po.tenantId = :tenantId
            ORDER BY po.createdAt DESC, po.id DESC
            """)
    List<POSummaryResponse> findDashboardRecentPosByTenant(
            @Param("tenantId") UUID tenantId,
            Pageable pageable
    );
}
