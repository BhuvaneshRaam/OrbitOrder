package com.hoodle.orbitorder.Controller;

import com.hoodle.orbitorder.DTO.PoUpdateRequest;
import com.hoodle.orbitorder.Entity.PurchaseOrder;
import com.hoodle.orbitorder.Service.POService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/po")
public class POController {

    @Autowired
    private POService poService;

    @PreAuthorize("hasAuthority('PROCUREMENT_MANAGER')")
    @PostMapping("/generate/{prqId}")
    public ResponseEntity<Map<String, Object>> generatePo(@PathVariable UUID prqId) {
        return ResponseEntity.ok(poService.generatePoFromPrq(prqId));
    }

    // PUT /api/v1/po/{poId}
    @PreAuthorize("hasAnyAuthority('PRQ_MANAGER', 'TENANT_ADMIN', 'PROCUREMENT_MANAGER')")
    @PutMapping("/{poId}")
    public ResponseEntity<Map<String, Object>> updateDraftPo(
            @PathVariable UUID poId,
            @RequestBody PoUpdateRequest request) {

        return ResponseEntity.ok(poService.updateDraftPo(poId, request));
    }

    @PreAuthorize("hasAnyAuthority('PRQ_MANAGER', 'TENANT_ADMIN', 'PROCUREMENT_MANAGER', 'EMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity<List<PurchaseOrder>> getAllPos() {
        return ResponseEntity.ok(poService.getAllPos());
    }

    @PreAuthorize("hasAnyAuthority('PRQ_MANAGER', 'TENANT_ADMIN', 'PROCUREMENT_MANAGER', 'EMPLOYEE')")
    @GetMapping("/{poId}")
    public ResponseEntity<PurchaseOrder> getPoById(@PathVariable UUID poId) {
        return ResponseEntity.ok(poService.getPoById(poId));
    }


}
