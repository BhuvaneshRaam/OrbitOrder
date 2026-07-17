package com.hoodle.orbitorder.Service;

import com.hoodle.orbitorder.DTO.*;
import com.hoodle.orbitorder.Entity.PRQ;
import com.hoodle.orbitorder.Entity.PurchaseOrder;
import com.hoodle.orbitorder.Entity.PurchaseOrderItem;
import com.hoodle.orbitorder.Enum.PoStatus;
import com.hoodle.orbitorder.Enum.PrStatus;
import com.hoodle.orbitorder.Exception.BusinessException;
import com.hoodle.orbitorder.Repository.PORepo;
import com.hoodle.orbitorder.Repository.PRQRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class POService {

    @Autowired
    private PRQRepo prqRepo;

    @Autowired
    private PORepo poRepo;


    @Transactional
    public Map<String, Object> generatePoFromPrq(UUID prqId) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 1. Fetch the PRQ (Ensuring it belongs to this tenant)
        PRQ prq = prqRepo.findByIdAndTenantId(prqId, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("PRQ not found", HttpStatus.NOT_FOUND));

        // 2. Business Rule: Only APPROVED PRQs can become POs
        if (prq.getStatus() != PrStatus.APPROVED) {
            throw new BusinessException("Only APPROVED Requisitions can be converted to Purchase Orders.", HttpStatus.BAD_REQUEST);
        }

        // 3. Business Rule: Prevent duplicate POs
        if (poRepo.existsByOriginalPrqId(prqId)) {
            throw new BusinessException("A Purchase Order already exists for this PRQ.", HttpStatus.CONFLICT);
        }

        // 4. Generate a unique PO Number
        String generatedPoNumber = "PO-" + java.time.Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        // 5. Build the Master PO
        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(generatedPoNumber)
                .tenantId(currentUser.tenantId())
                .originalPrq(prq)
                .status(PoStatus.DRAFT) // Starts as DRAFT so Procurement can add vendor details later
                .totalAmount(prq.getTotalAmount()) // Initially copy the estimated amount
                .createdBy(currentUser.userId())
                .build();

        // 6. Deep Copy the Items from PRQ to PO
        List<PurchaseOrderItem> poItems = prq.getItems().stream().map(prqItem ->
                PurchaseOrderItem.builder()
                        .purchaseOrder(po) // Link back to the parent PO
                        .itemName(prqItem.getItemName())
                        .description(prqItem.getDescription())
                        .quantity(prqItem.getQuantity())
                        .unitPrice(prqItem.getUnitPrice())
                        .lineAmount(prqItem.getLineAmount())
                        .build()
        ).collect(Collectors.toList());

        po.setItems(poItems);

        // 7. Save to Database
        PurchaseOrder savedPo = poRepo.save(po);

        return Map.of(
                "message", "Purchase Order generated successfully",
                "poId", savedPo.getId(),
                "poNumber", savedPo.getPoNumber(),
                "status", savedPo.getStatus().name()
        );
    }


    @Transactional
    public Map<String, Object> updateDraftPo(UUID poId, PoUpdateRequest request) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 1. Fetch the PO
        PurchaseOrder po = poRepo.findByIdAndTenantId(poId, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("Purchase Order not found", HttpStatus.NOT_FOUND));

        // 2. Business Rule: Only DRAFT POs can be edited
        if (po.getStatus() != PoStatus.DRAFT) {
            throw new BusinessException("Only DRAFT Purchase Orders can be updated.", HttpStatus.BAD_REQUEST);
        }

        // 3. Update editable PO fields only
        po.setVendorName(request.getVendorName());
        po.setVendorEmail(request.getVendorEmail());
        po.setNegotiatedPrice(request.getNegotiatedPrice());

        // 4. Save and Return
        poRepo.save(po);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Purchase Order updated successfully");
        response.put("poNumber", po.getPoNumber());
        response.put("vendorName", po.getVendorName());
        response.put("vendorEmail", po.getVendorEmail());
        response.put("negotiatedPrice", po.getNegotiatedPrice());
        return response;
    }


    // --- Get All POs ---
    public Page<POSummaryResponse> getAllPos(int page, int size) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        Page<PurchaseOrder> poPage =  poRepo.findAllByTenantIdOrderByCreatedAtDesc(currentUser.tenantId(), pageable);

        return poPage.map(this::mapToSummary);
    }

    // --- Get PO by ID ---
    public PurchaseOrder getPoById(UUID poId) {
        UserContext currentUser = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return poRepo.findByIdAndTenantId(poId, currentUser.tenantId())
                .orElseThrow(() -> new BusinessException("Purchase Order not found", HttpStatus.NOT_FOUND));
    }

    private POSummaryResponse mapToSummary(PurchaseOrder po) {
        return POSummaryResponse.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .vendorName(po.getVendorName())
                .status(po.getStatus().name())
                .totalAmount(po.getTotalAmount())
                .prNumber(po.getOriginalPrq().getPrNumber())
                .createdAt(po.getCreatedAt())
                .build();
    }

}
