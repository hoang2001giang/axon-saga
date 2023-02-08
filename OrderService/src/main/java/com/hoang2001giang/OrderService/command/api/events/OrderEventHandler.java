package com.hoang2001giang.OrderService.command.api.events;

import com.hoang2001giang.CommonService.events.*;
import com.hoang2001giang.OrderService.command.api.data.Order;
import com.hoang2001giang.OrderService.command.api.data.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@ProcessingGroup("order")
public class OrderEventHandler {
    private OrderRepository orderRepository;

    @Autowired
    private EventGateway eventGateway;

    @Autowired
    private QueryGateway queryGateway;


    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        Order order = new Order();
        BeanUtils.copyProperties(orderCreatedEvent,order);
        orderRepository.save(order);

        log.info("OrderCreatedEvent for Order ID: {}", order.getOrderId());
        log.info(String.valueOf(orderCreatedEvent));

    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        Order order
                = orderRepository.findById(event.getOrderId()).get();

        order.setOrderStatus(event.getOrderStatus());

        orderRepository.save(order);

        log.info("OrderCompletedEvent for Order ID: {}", order.getOrderId());
        log.info(String.valueOf(event));

    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        Order order
                = orderRepository.findById(event.getOrderId()).get();

        order.setOrderStatus(event.getOrderStatus());

        orderRepository.save(order);

        log.info("OrderCancelledEvent for Order ID: {}", order.getOrderId());
        log.info(String.valueOf(event));

    }

    @EventHandler
    public void on(PaymentCancelledEvent paymentCancelledEvent) {
        log.info("PaymentCancelledEvent for Order ID: {}", paymentCancelledEvent.getOrderId());
        log.info(String.valueOf(paymentCancelledEvent));

        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(paymentCancelledEvent.getOrderId());
        eventGateway.publish(orderCancelledEvent);

    }

    @EventHandler
    public void on(OrderShippedEvent orderShippedEvent) {

        log.info("OrderShippedEvent for Order ID: {}", orderShippedEvent.getOrderId());
        log.info(String.valueOf(orderShippedEvent));

        try {

//            if(true) throw new Exception();

            OrderCompletedEvent orderCompletedEvent =
                    new OrderCompletedEvent(orderShippedEvent.getOrderId());

            eventGateway.publish(orderCompletedEvent);


        } catch (Exception e) {
            log.error(e.getMessage());
            // start compensating action

        }
    }


}
