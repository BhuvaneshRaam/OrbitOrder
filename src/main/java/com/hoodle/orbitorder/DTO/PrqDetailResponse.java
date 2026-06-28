package com.hoodle.orbitorder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class PrqDetailResponse {
    private UUID id;
    private String prNumber;
    private String department;
    private String remarks;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<PrqItemResponse> items;
}
