package com.hoodle.orbitorder.Repository;

import com.hoodle.orbitorder.DTO.VendorListResponse;
import com.hoodle.orbitorder.DTO.VendorSummaryResponse;
import com.hoodle.orbitorder.Entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendorRepo extends JpaRepository<Vendor, UUID> {
    Optional<Vendor> findByVendorUuidAndTenantId(UUID vendorUuid, UUID tenantId);

    @Query(value = """
            SELECT new com.hoodle.orbitorder.DTO.VendorSummaryResponse(
                v.vendorUuid, v.vendorName, v.contactEmail, v.isActive
            )
            FROM Vendor v
            WHERE v.tenantId = :tenantId
              AND (
                  :search IS NULL OR :search = ''
                  OR LOWER(v.vendorName) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(v.contactEmail) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            ORDER BY v.vendorName ASC, v.vendorUuid ASC
            """,
            countQuery = """
                    SELECT COUNT(v)
                    FROM Vendor v
                    WHERE v.tenantId = :tenantId
                      AND (
                          :search IS NULL OR :search = ''
                          OR LOWER(v.vendorName) LIKE LOWER(CONCAT('%', :search, '%'))
                          OR LOWER(v.contactEmail) LIKE LOWER(CONCAT('%', :search, '%'))
                      )
                    """)
    Page<VendorSummaryResponse> searchVendorSummariesByTenant(
            @Param("tenantId") UUID tenantId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT new com.hoodle.orbitorder.DTO.VendorListResponse(
                v.vendorUuid, v.vendorName, v.contactEmail
            )
            FROM Vendor v
            WHERE v.tenantId = :tenantId
              AND v.isActive = true
            ORDER BY v.vendorName ASC, v.vendorUuid ASC
            """)
    List<VendorListResponse> findActiveVendorListByTenant(@Param("tenantId") UUID tenantId);
}
