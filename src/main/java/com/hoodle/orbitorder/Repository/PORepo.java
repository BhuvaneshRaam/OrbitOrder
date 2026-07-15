package com.hoodle.orbitorder.Repository;

import com.hoodle.orbitorder.Entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PORepo extends JpaRepository<PurchaseOrder, UUID> {

    boolean existsByOriginalPrqId(UUID prqId);

    Optional<PurchaseOrder> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<PurchaseOrder> findAllByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);
}
