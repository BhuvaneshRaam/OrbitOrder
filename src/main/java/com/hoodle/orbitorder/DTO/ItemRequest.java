package com.hoodle.orbitorder.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    private Long productId;
    private BigDecimal  quantity;
    private BigDecimal price;
}
