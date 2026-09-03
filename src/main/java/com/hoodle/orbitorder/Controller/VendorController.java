package com.hoodle.orbitorder.Controller;

import com.hoodle.orbitorder.DTO.VendorCreateRequest;
import com.hoodle.orbitorder.DTO.VendorDetailResponse;
import com.hoodle.orbitorder.DTO.VendorListResponse;
import com.hoodle.orbitorder.DTO.VendorSummaryResponse;
import com.hoodle.orbitorder.DTO.VendorUpdateRequest;
import com.hoodle.orbitorder.Service.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createVendor(
            @Valid @RequestBody VendorCreateRequest request) {
        return ResponseEntity.status(201).body(vendorService.createVendor(request));
    }

    @PostMapping("/{vendorUuid}")
    public ResponseEntity<Map<String, Object>> updateVendor(
            @PathVariable UUID vendorUuid,
            @Valid @RequestBody VendorUpdateRequest request) {
        return ResponseEntity.ok(vendorService.updateVendor(vendorUuid, request));
    }

    @GetMapping("/{vendorUuid}")
    public ResponseEntity<VendorDetailResponse> getVendorByUuid(@PathVariable UUID vendorUuid) {
        return ResponseEntity.ok(vendorService.getVendorByUuid(vendorUuid));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllVendors(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok("ORBIT ORDER REACHED SUCCESSFULLY");
    }

    @GetMapping("/list")
    public ResponseEntity<List<VendorListResponse>> getActiveVendorList() {
        return ResponseEntity.ok(vendorService.getActiveVendorList());
    }
}
