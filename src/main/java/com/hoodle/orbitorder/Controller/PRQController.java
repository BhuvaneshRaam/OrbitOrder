package com.hoodle.orbitorder.Controller;

import com.hoodle.orbitorder.DTO.PrRequest;
import com.hoodle.orbitorder.DTO.PrqDetailResponse;
import com.hoodle.orbitorder.DTO.PrqSummaryResponse;
import com.hoodle.orbitorder.Service.PRQService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasAuthority;

@RestController
@RequestMapping("/api/v1/prq")
public class PRQController {

    @Autowired
    private PRQService prqService;

    @PostMapping("/create")
    public ResponseEntity<Map<String,Object>> createPrq(@RequestBody PrRequest request) {
        Map<String,Object> response = prqService.createDraftPurchaseRequest(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<PrqSummaryResponse>> getUserPrq(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PrqSummaryResponse> response = prqService.getUserPurchaseRequests(page, size);
        return ResponseEntity.ok(response);
    }

    // --- 3. GET: All PRQs (Summary List with Optional Filter) ---
    @GetMapping("/all")
    public ResponseEntity<Page<PrqSummaryResponse>> getAllPrq(
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PrqSummaryResponse> response = prqService.getAllPurchaseRequests(department, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrqDetailResponse> getPrqById(@PathVariable UUID id) {
        PrqDetailResponse response = prqService.getPurchaseRequestById(id);
        return ResponseEntity.ok(response);
    }

    // --- 5. POST: Submit a Draft ---
    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, String>> submitPrq(@PathVariable UUID id) {
        return ResponseEntity.ok(prqService.submitPrq(id));
    }

    // --- 6. POST: Approve a PRQ (Manager Only) ---
    @PreAuthorize("hasAuthority('PROCUREMENT_MANAGER')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, String>> approvePrq(@PathVariable UUID id) {
        return ResponseEntity.ok(prqService.approvePrq(id));
    }

    // --- 7. POST: Reject a PRQ (Manager Only) ---
    @PreAuthorize("hasAuthority('PROCUREMENT_MANAGER')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectPrq(@PathVariable UUID id) {
        return ResponseEntity.ok(prqService.rejectPrq(id));
    }

    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("Authorities inside Controller: " + auth.getAuthorities());
    }
}
