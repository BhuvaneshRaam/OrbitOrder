package com.hoodle.orbitorder.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hoodle.orbitorder.Enum.PoStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="purchase_order")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="po_number", unique = true, nullable = false)
    private String poNumber;

    @Column(name="tenant_id", nullable = false)
    private UUID tenantId;

    // 1-to-1 Mapping: Locks the PO to the original Requisition
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prq_id", unique = true, nullable = false)
    @JsonIgnoreProperties({"items", "hibernateLazyInitializer"})
    private PRQ originalPrq;

    // External Vendor Details
    @Column(name="vendor_name")
    private String vendorName;

    @Column(name="vendor_email")
    private String vendorEmail;

    @Column(name = "negotiated_price")
    private BigDecimal negotiatedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private PoStatus status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name="created_by")
    private UUID createdBy; // The Procurement Manager who generated it

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
