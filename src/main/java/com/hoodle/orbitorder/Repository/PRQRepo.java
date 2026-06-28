package com.hoodle.orbitorder.Repository;

import com.hoodle.orbitorder.Entity.PRQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
