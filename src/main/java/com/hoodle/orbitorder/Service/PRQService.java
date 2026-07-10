package com.hoodle.orbitorder.Service;

import com.hoodle.orbitorder.DTO.*;
import com.hoodle.orbitorder.Entity.PRQ;
import com.hoodle.orbitorder.Entity.PRQItems;
import com.hoodle.orbitorder.Enum.PrStatus;
import com.hoodle.orbitorder.Exception.BusinessException;
import com.hoodle.orbitorder.Repository.PRQRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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


    public Page<PrqSummaryResponse> getUserPurchaseRequests(int page, int size) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PRQ> userPrqs = prqRepo.findByRequesterIdAndTenantId(currentUser.userId(), currentUser.tenantId(), pageable);
        return userPrqs.map(this::mapToSummary);
    }

    public Page<PrqSummaryResponse> getAllPurchaseRequests(String department, int page, int size) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PRQ> prqPage = (department != null && !department.isBlank())
                ? prqRepo.findByTenantIdAndDepartment(currentUser.tenantId(), department, pageable)
                : prqRepo.findByTenantId(currentUser.tenantId(), pageable);

        return prqPage.map(this::mapToSummary);
    }

    public PrqDetailResponse getPurchaseRequestById(UUID prqId) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PRQ prq = prqRepo.findByIdAndTenantId(prqId, currentUser.tenantId())
                .orElseThrow(() -> new RuntimeException("PRQ not found or access denied"));

        return mapToDetail(prq);
    }

    @Transactional
    public Map<String, String> submitPrq(UUID prqId) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 1. Find the PRQ (Ensuring it belongs to the current user's tenant)
        PRQ prq = prqRepo.findByIdAndTenantId(prqId, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("PRQ not found", HttpStatus.NOT_FOUND));

        // 2. Security Check: Only the person who created it can submit it
        if (!prq.getRequesterId().equals(currentUser.userId())) {
            throw new BusinessException("Unauthorized: You can only submit your own Drafts.", HttpStatus.FORBIDDEN);
        }

        // 3. Business Logic Check: Can only submit if it's a DRAFT
        if (prq.getStatus() != PrStatus.DRAFT) {
            throw new BusinessException("Only DRAFT requests can be submitted.", HttpStatus.BAD_REQUEST);
        }

        // 4. Update the state
        prq.setStatus(PrStatus.SUBMITTED);
        prqRepo.save(prq);

        return Map.of("message", "PRQ successfully submitted", "status", prq.getStatus().name());
    }

    @Transactional
    public Map<String, String> approvePrq(UUID prqId) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PRQ prq = prqRepo.findByIdAndTenantId(prqId, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("PRQ not found", HttpStatus.NOT_FOUND));

        if (prq.getStatus() != PrStatus.SUBMITTED) {
            throw new BusinessException("Only SUBMITTED requests can be approved.", HttpStatus.BAD_REQUEST);
        }

        // Update state and log the Manager's ID!
        prq.setStatus(PrStatus.APPROVED);
        prq.setApproverId(currentUser.userId());
        prqRepo.save(prq);

        return Map.of("message", "PRQ successfully approved", "status", prq.getStatus().name());
    }

    @Transactional
    public Map<String,String> updatePrq(UUID prqId, PrRequest request) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PRQ prq = prqRepo.findByIdAndTenantId(prqId, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("PRQ not found", HttpStatus.NOT_FOUND));

        if (!prq.getRequesterId().equals(currentUser.userId())) {
            throw new BusinessException("Unauthorized: You can only edit your own Drafts.", HttpStatus.FORBIDDEN);
        }

        // 3. Business Logic Check: Can only edit if it's a DRAFT
        if (prq.getStatus() != PrStatus.DRAFT) {
            throw new BusinessException("Only DRAFT requests can be edited.", HttpStatus.BAD_REQUEST);
        }

        prq.setDepartment(request.getDepartment());
        prq.setRemarks(request.getRemarks());

        // 4. Clear the existing items (Hibernate will delete them from the DB automatically)
        prq.getItems().clear();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 5. Loop through the incoming request and rebuild the items list
        for (PrItemRequest itemReq : request.getItems()) {
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

            prq.getItems().add(item);
        }

        // 6. Update the new total amount
        prq.setTotalAmount(totalAmount);

        // 7. Save to the database
        PRQ savedPrq = prqRepo.save(prq);

        return Map.of(
                "prNumber", savedPrq.getPrNumber(),
                "status", savedPrq.getStatus().name()
        );


    }

    @Transactional
    public Map<String, String> rejectPrq(UUID prqId) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PRQ prq = prqRepo.findByIdAndTenantId(prqId, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("PRQ not found", HttpStatus.NOT_FOUND));

        if (prq.getStatus() != PrStatus.SUBMITTED) {
            throw new BusinessException("Only SUBMITTED requests can be rejected.", HttpStatus.BAD_REQUEST);
        }

        // Update state and log the Manager's ID!
        prq.setStatus(PrStatus.REJECTED);
        prq.setApproverId(currentUser.userId());
        prqRepo.save(prq);

        return Map.of("message", "PRQ successfully approved", "status", prq.getStatus().name());
    }

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
