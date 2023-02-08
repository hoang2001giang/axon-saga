package com.hoang2001giang.OrderService.command.api.events;

import com.hoang2001giang.CommonService.events.OrderCompletedEvent;
import com.hoang2001giang.OrderService.command.api.data.Order;
import com.hoang2001giang.OrderService.command.api.data.OrderRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsHandler {
    private OrderRepository orderRepository;

    public OrderEventsHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        Order order = new Order();
        BeanUtils.copyProperties(event, order);
        orderRepository.save(order);
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).get();
        order.setOrderStatus(event.getOrderStatus());
        orderRepository.save(order);
    }

    public void on(OrderCancelledEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).get();
        order.setOrderStatus(event.getOrderStatus());
        orderRepository.save(order);
    }
}
