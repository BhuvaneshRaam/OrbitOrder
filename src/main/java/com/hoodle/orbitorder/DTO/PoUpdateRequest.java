package com.hoodle.orbitorder.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PoUpdateRequest {
    private String vendorName;
    private String vendorEmail;
    private BigDecimal negotiatedPrice;
}
