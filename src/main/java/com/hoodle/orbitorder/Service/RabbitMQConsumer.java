package com.hoodle.orbitorder.Service;

import com.hoodle.orbitorder.Config.RabbitMQConfig;
import com.hoodle.orbitorder.DTO.PaymentSuccessEvent;
import com.hoodle.orbitorder.Entity.Orders;
import com.hoodle.orbitorder.Repository.OrderRepo;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    @Autowired
    OrderRepo orderRepo;

//    @RabbitListener(queues = RabbitMQConfig.QUEUE)
//    public void handlePaymentSuccess(PaymentSuccessEvent event) {
//        Orders order = orderRepo.findById(event.getOrderId())
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        order.setOrderStatus("PAID");
//        orderRepo.save(order);
//
//        System.out.println("Order " + event.getOrderId() + " marked as PAID");
//    }
}
