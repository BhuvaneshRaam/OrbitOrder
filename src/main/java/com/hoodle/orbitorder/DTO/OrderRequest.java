package com.hoodle.orbitorder.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long id;
    private List<ItemRequest> items;
    private String userId;
    private BigDecimal totalAmount;
    private String orderStatus;
}
