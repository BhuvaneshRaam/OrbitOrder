package com.hoodle.orbitorder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class POSummaryResponse {
    private UUID id;
    private String poNumber;
    private String vendorName;
    private String status;
    private BigDecimal totalAmount;
    private String prNumber;
    private LocalDateTime createdAt;

}
