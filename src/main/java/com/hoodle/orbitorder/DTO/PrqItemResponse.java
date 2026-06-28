package com.hoodle.orbitorder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class PrqItemResponse {
    private UUID id;
    private String itemName;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
}
