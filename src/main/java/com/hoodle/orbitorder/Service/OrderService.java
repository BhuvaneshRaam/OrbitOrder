package com.hoodle.orbitorder.Service;

import com.hoodle.orbitorder.DTO.ItemRequest;
import com.hoodle.orbitorder.DTO.OrderRequest;
import com.hoodle.orbitorder.Entity.OrderItems;
import com.hoodle.orbitorder.Entity.Orders;
import com.hoodle.orbitorder.Repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    public OrderRequest createOrder(OrderRequest orderRequest) {
        Orders order = Orders.builder()
                        .userId(orderRequest.getUserId())
                        .orderStatus("CREATED")
                        .build();
        List<OrderItems> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for(ItemRequest req: orderRequest.getItems()) {
            OrderItems item = OrderItems.builder()
                    .productId(req.getProductId())
                    .price(req.getPrice())
                    .quantity(req.getQuantity())
                    .order(order)
                    .build();
            items.add(item);
            BigDecimal itemTotal = req.getPrice().multiply(req.getQuantity());
            total = total.add(itemTotal);

        }
        order.setItems(items);
        order.setTotalAmount(total);
        Orders savedOrder = orderRepo.save(order);

        return mapToResponse(savedOrder);



    }

    private OrderRequest mapToResponse(Orders order) {
        return OrderRequest.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .items(
                        order.getItems().stream()
                                .map(item -> ItemRequest.builder()
                                        .productId(item.getProductId())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build()
                                ).toList()
                )
                .build();
    }

    public OrderRequest getOrderById(Long id) {

        Orders order = orderRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order doesn't exist!")
                );

        return mapToResponse(order);
    }


    public List<OrderRequest> getAllOrders() {
        List<Orders> orders = new ArrayList<>();
        try {
            orders = orderRepo.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException("Unexcpected error occured while fetching orders");
        }
        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }
}
