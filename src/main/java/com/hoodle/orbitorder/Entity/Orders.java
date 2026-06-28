package com.hoodle.orbitorder.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private String userId;

    @Column(name="total_amount")
    private BigDecimal totalAmount;

    @Column(name="order_status")
    private String orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItems> items;
}
