package com.hoodle.orbitorder.Service;

import com.hoodle.orbitorder.DTO.UserContext;
import com.hoodle.orbitorder.DTO.VendorCreateRequest;
import com.hoodle.orbitorder.DTO.VendorDetailResponse;
import com.hoodle.orbitorder.DTO.VendorListResponse;
import com.hoodle.orbitorder.DTO.VendorSummaryResponse;
import com.hoodle.orbitorder.DTO.VendorUpdateRequest;
import com.hoodle.orbitorder.Entity.Vendor;
import com.hoodle.orbitorder.Exception.BusinessException;
import com.hoodle.orbitorder.Repository.VendorRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VendorService {

    @Autowired
    private VendorRepo vendorRepo;

    @Transactional
    public Map<String, Object> createVendor(VendorCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserContext)) {
            throw new BusinessException("Unauthorized: Valid security context is missing!", HttpStatus.UNAUTHORIZED);
        }

        UserContext currentUser = (UserContext) authentication.getPrincipal();

        Vendor vendor = Vendor.builder()
                .vendorName(request.getVendorName())
                .contactPerson(request.getContactPerson())
                .contactEmail(request.getEmailId())
                .contactPhone(request.getContactPhone())
                .gstin(request.getGstin())
                .pan(request.getPan())
                .billingAddress(request.getBillingAddress())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .tenantId(currentUser.tenantId())
                .build();

        Vendor savedVendor = vendorRepo.save(vendor);

        return Map.of(
                "message", "Vendor created successfully",
                "vendorUuid", savedVendor.getVendorUuid(),
                "isActive", savedVendor.isActive()
        );
    }

    @Transactional
    public Map<String, Object> updateVendor(UUID vendorUuid, VendorUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserContext)) {
            throw new BusinessException("Unauthorized: Valid security context is missing!", HttpStatus.UNAUTHORIZED);
        }

        UserContext currentUser = (UserContext) authentication.getPrincipal();

        Vendor vendor = vendorRepo.findByVendorUuidAndTenantId(vendorUuid, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("Vendor not found", HttpStatus.NOT_FOUND));

        vendor.setVendorName(request.getVendorName());
        vendor.setContactPerson(request.getContactPerson());
        vendor.setContactEmail(request.getEmailId());
        vendor.setContactPhone(request.getContactPhone());
        vendor.setGstin(request.getGstin());
        vendor.setPan(request.getPan());
        vendor.setBillingAddress(request.getBillingAddress());

        if (request.getIsActive() != null) {
            vendor.setActive(request.getIsActive());
        }

        Vendor savedVendor = vendorRepo.save(vendor);

        return Map.of(
                "message", "Vendor updated successfully",
                "vendorUuid", savedVendor.getVendorUuid(),
                "isActive", savedVendor.isActive()
        );
    }

    public VendorDetailResponse getVendorByUuid(UUID vendorUuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserContext)) {
            throw new BusinessException("Unauthorized: Valid security context is missing!", HttpStatus.UNAUTHORIZED);
        }

        UserContext currentUser = (UserContext) authentication.getPrincipal();

        Vendor vendor = vendorRepo.findByVendorUuidAndTenantId(vendorUuid, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("Vendor not found", HttpStatus.NOT_FOUND));

        return VendorDetailResponse.builder()
                .vendorUuid(vendor.getVendorUuid())
                .vendorName(vendor.getVendorName())
                .contactPerson(vendor.getContactPerson())
                .emailId(vendor.getContactEmail())
                .contactPhone(vendor.getContactPhone())
                .gstin(vendor.getGstin())
                .pan(vendor.getPan())
                .billingAddress(vendor.getBillingAddress())
                .isActive(vendor.isActive())
                .build();
    }

    public Page<VendorSummaryResponse> getAllVendors(String search, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserContext)) {
            throw new BusinessException("Unauthorized: Valid security context is missing!", HttpStatus.UNAUTHORIZED);
        }

        UserContext currentUser = (UserContext) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        return vendorRepo.searchVendorSummariesByTenant(currentUser.tenantId(), search, pageable);
    }

    public List<VendorListResponse> getActiveVendorList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserContext)) {
            throw new BusinessException("Unauthorized: Valid security context is missing!", HttpStatus.UNAUTHORIZED);
        }

        UserContext currentUser = (UserContext) authentication.getPrincipal();
        return vendorRepo.findActiveVendorListByTenant(currentUser.tenantId());
    }
}
