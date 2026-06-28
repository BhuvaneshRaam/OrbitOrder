package com.hoodle.orbitorder.Service;

import com.hoodle.orbitorder.DTO.*;
import com.hoodle.orbitorder.Entity.PRQ;
import com.hoodle.orbitorder.Entity.PRQItems;
import com.hoodle.orbitorder.Enum.PrStatus;
import com.hoodle.orbitorder.Repository.PRQRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PRQService {

    @Autowired
    private PRQRepo prqRepo;

    @Transactional
    public Map<String, Object> createDraftPurchaseRequest(PrRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserContext)) {
            throw new RuntimeException("Unauthorized: Valid security context is missing!");
        }

        UserContext currentUser = (UserContext) authentication.getPrincipal();

        String prNumber = "PR_" + Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0,4).toUpperCase();

        PRQ prq = PRQ.builder()
                .prNumber(prNumber)
                .tenantId(currentUser.tenantId())
                .requesterId(currentUser.userId())
                .department(request.getDepartment())
                .remarks(request.getRemarks())
                .status(PrStatus.DRAFT)
                .build();

        List<PRQItems> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PrItemRequest itemReq: request.getItems()) {
            BigDecimal lineAmount = itemReq.getUnitPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            totalAmount = totalAmount.add(lineAmount);

            PRQItems item = PRQItems.builder()
                    .itemName(itemReq.getItemName())
                    .description(itemReq.getDescription())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .lineAmount(lineAmount)
                    .prq(prq)
                    .build();

            items.add(item);
        }
        prq.setItems(items);
        prq.setTotalAmount(totalAmount);

        PRQ savedPrq = prqRepo.save(prq);

        return Map.of("prNumber",savedPrq.getPrNumber(), "status", savedPrq.getStatus() );
    }


    // --- 1. GET MY LIST (Summary) ---
    public Page<PrqSummaryResponse> getUserPurchaseRequests(int page, int size) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PRQ> userPrqs = prqRepo.findByRequesterIdAndTenantId(currentUser.userId(), currentUser.tenantId(), pageable);
        return userPrqs.map(this::mapToSummary);
    }

    // --- 2. GET ALL LIST (Summary) ---
    public Page<PrqSummaryResponse> getAllPurchaseRequests(String department, int page, int size) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PRQ> prqPage = (department != null && !department.isBlank())
                ? prqRepo.findByTenantIdAndDepartment(currentUser.tenantId(), department, pageable)
                : prqRepo.findByTenantId(currentUser.tenantId(), pageable);

        return prqPage.map(this::mapToSummary);
    }

    // --- 3. GET SINGLE DETAIL (Heavy) ---
    public PrqDetailResponse getPurchaseRequestById(UUID prqId) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PRQ prq = prqRepo.findByIdAndTenantId(prqId, currentUser.tenantId())
                .orElseThrow(() -> new RuntimeException("PRQ not found or access denied"));

        return mapToDetail(prq);
    }

    // --- MAPPERS ---
    private PrqSummaryResponse mapToSummary(PRQ prq) {
        return PrqSummaryResponse.builder()
                .id(prq.getId())
                .prNumber(prq.getPrNumber())
                .department(prq.getDepartment())
                .status(prq.getStatus().name())
                .totalAmount(prq.getTotalAmount())
                .createdAt(prq.getCreatedAt())
                .build();
    }

    private PrqDetailResponse mapToDetail(PRQ prq) {
        List<PrqItemResponse> itemResponses = prq.getItems().stream().map(item ->
                PrqItemResponse.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .lineAmount(item.getLineAmount())
                        .build()
        ).toList();

        return PrqDetailResponse.builder()
                .id(prq.getId())
                .prNumber(prq.getPrNumber())
                .department(prq.getDepartment())
                .remarks(prq.getRemarks())
                .status(prq.getStatus().name())
                .totalAmount(prq.getTotalAmount())
                .createdAt(prq.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
