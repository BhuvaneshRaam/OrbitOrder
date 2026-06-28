package com.hoodle.orbitorder.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PrItemRequest {
    private String itemName;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
}
