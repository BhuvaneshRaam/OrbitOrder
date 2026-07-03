package com.hoodle.orbitorder.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="purchase_order_items")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    @JsonIgnore
    private PurchaseOrder purchaseOrder;

    private String itemName;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
}
