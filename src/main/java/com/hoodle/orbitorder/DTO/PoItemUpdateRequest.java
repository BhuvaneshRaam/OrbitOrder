package com.hoodle.orbitorder.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PoItemUpdateRequest {
    private UUID id;
    private BigDecimal negotiatedPrice;
}
