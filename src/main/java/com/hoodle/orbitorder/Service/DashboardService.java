package com.hoodle.orbitorder.Service;

import com.hoodle.orbitorder.DTO.ApprovalMetricsResponse;
import com.hoodle.orbitorder.DTO.DashboardSummaryResponse;
import com.hoodle.orbitorder.DTO.PersonalPrqMetricsResponse;
import com.hoodle.orbitorder.DTO.PoMetricsResponse;
import com.hoodle.orbitorder.DTO.UserContext;
import com.hoodle.orbitorder.DTO.VendorMetricsResponse;
import com.hoodle.orbitorder.Enum.PoStatus;
import com.hoodle.orbitorder.Enum.PrStatus;
import com.hoodle.orbitorder.Exception.BusinessException;
import com.hoodle.orbitorder.Repository.PORepo;
import com.hoodle.orbitorder.Repository.PRQRepo;
import com.hoodle.orbitorder.Repository.VendorRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class DashboardService {

    private static final int DASHBOARD_LIST_SIZE = 5;

    private final PRQRepo prqRepo;
    private final PORepo poRepo;
    private final VendorRepo vendorRepo;

    public DashboardService(PRQRepo prqRepo, PORepo poRepo, VendorRepo vendorRepo) {
        this.prqRepo = prqRepo;
        this.poRepo = poRepo;
        this.vendorRepo = vendorRepo;
    }

    public DashboardSummaryResponse getSummary(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserContext currentUser)) {
            throw new BusinessException("Unauthorized: Valid security context is missing!", UNAUTHORIZED);
        }

        UUID userId = currentUser.userId();
        UUID tenantId = currentUser.tenantId();
        PageRequest latestFive = PageRequest.of(0, DASHBOARD_LIST_SIZE);
        PageRequest oldestFive = PageRequest.of(0, DASHBOARD_LIST_SIZE);

        DashboardSummaryResponse.DashboardSummaryResponseBuilder response = DashboardSummaryResponse.builder();
        Long myPendingRequests = null;
        Long myRejectedRequests = null;
        Long totalApprovedPrqs = null;

        if (hasAuthority(authentication, "PURCHASE_REQUESTS.READ")) {
            myPendingRequests = prqRepo.countByRequesterIdAndTenantIdAndStatus(userId, tenantId, PrStatus.SUBMITTED);
            myRejectedRequests = prqRepo.countByRequesterIdAndTenantIdAndStatus(userId, tenantId, PrStatus.REJECTED);
            response.recentPrqs(prqRepo.findDashboardRecentPrqsByRequesterAndTenant(userId, tenantId, latestFive));
        }

        if (hasAuthority(authentication, "PURCHASE_REQUESTS.READ_ALL")) {
            totalApprovedPrqs = prqRepo.countByTenantIdAndStatus(tenantId, PrStatus.APPROVED);
            response.bottleneckPrqs(prqRepo.findDashboardPrqsByTenantAndStatusOldestFirst(
                    tenantId, PrStatus.SUBMITTED, oldestFive));
        }

        if (myPendingRequests != null || myRejectedRequests != null || totalApprovedPrqs != null) {
            response.personalPrqMetrics(PersonalPrqMetricsResponse.builder()
                    .myPendingRequests(myPendingRequests)
                    .myRejectedRequests(myRejectedRequests)
                    .totalApprovedPrqs(totalApprovedPrqs)
                    .build());
        }

        if (hasAuthority(authentication, "PURCHASE_REQUESTS.APPROVE")) {
            response.approvalMetrics(ApprovalMetricsResponse.builder()
                    .pendingApprovals(prqRepo.countByTenantIdAndStatus(tenantId, PrStatus.SUBMITTED))
                    .build());
        }

        if (hasAuthority(authentication, "PURCHASE_ORDERS.READ")) {
            LocalDate currentDate = LocalDate.now();
            LocalDateTime startOfMonth = currentDate.withDayOfMonth(1).atStartOfDay();
            LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

            BigDecimal monthlySpend = poRepo.calculateDashboardMonthlySpendByTenant(
                    tenantId,
                    startOfMonth,
                    startOfNextMonth,
                    List.of(PoStatus.ISSUED, PoStatus.DELIVERED, PoStatus.PAID));

            response.poMetrics(PoMetricsResponse.builder()
                    .monthlyPoSpend(monthlySpend == null ? BigDecimal.ZERO : monthlySpend)
                    .build())
                    .recentPos(poRepo.findDashboardRecentPosByTenant(tenantId, latestFive));
        }

        response.vendorMetrics(VendorMetricsResponse.builder()
                .activeVendors(vendorRepo.countByTenantIdAndIsActiveTrue(tenantId))
                .build());

        return response.build();
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }
}
