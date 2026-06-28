package com.hoodle.orbitorder.Controller;

import com.hoodle.orbitorder.DTO.OrderRequest;
import com.hoodle.orbitorder.Entity.Orders;
import com.hoodle.orbitorder.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/createOrder")
    public ResponseEntity<OrderRequest> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.status(201).body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequest> getOrderByID(@PathVariable Long id){
        return ResponseEntity.status(200).body(orderService.getOrderById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderRequest>> getAllOrders() {
        return ResponseEntity.status(200).body(orderService.getAllOrders());
    }


}
