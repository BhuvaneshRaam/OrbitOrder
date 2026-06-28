package com.hoodle.orbitorder.Entity;

import com.hoodle.orbitorder.Enum.PrStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="purchase_request")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PRQ {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pr_number", unique = true, nullable = false)
    private String prNumber;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name="requester_id", nullable = false)
    private UUID requesterId;

    @Column(name="approver_id")
    private UUID approverId;

    @Column(name = "department")
    private String department;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PrStatus status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One PRQ has many Items
    @OneToMany(mappedBy = "prq", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PRQItems> items;

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
