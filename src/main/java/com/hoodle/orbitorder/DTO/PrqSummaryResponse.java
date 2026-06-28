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
public class PrqSummaryResponse {
    private UUID id;
    private String prNumber;
    private String department;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
