package com.hoang2001giang.OrderService.command.api.aggregate;

import com.hoang2001giang.CommonService.commands.CancelOrderCommand;
import com.hoang2001giang.CommonService.commands.CancelPaymentCommand;
import com.hoang2001giang.CommonService.commands.CompleteOrderCommand;
import com.hoang2001giang.CommonService.events.OrderCompletedEvent;
import com.hoang2001giang.OrderService.command.api.command.CreateOrderCommand;
import com.hoang2001giang.OrderService.command.api.events.OrderCancelledEvent;
import com.hoang2001giang.OrderService.command.api.events.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {
    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;

    public OrderAggregate() {

    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.addressId = event.getAddressId();
        this.productId = event.getProductId();
        this.orderStatus = event.getOrderStatus();
        this.quantity = event.getQuantity();
    }

    @CommandHandler
    public void handle(CompleteOrderCommand completeOrderCommand) {
        // validate the command
        // publish order completed event
        OrderCompletedEvent orderCompletedEvent =
                OrderCompletedEvent.builder()
                        .orderId(completeOrderCommand.getOrderId())
                        .orderStatus(completeOrderCommand.getOrderStatus())
                        .build();

        AggregateLifecycle.apply(orderCompletedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {
        this.orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(CancelOrderCommand cancelOrderCommand) {
        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent();
        BeanUtils.copyProperties(cancelOrderCommand, orderCancelledEvent);
        AggregateLifecycle.apply(orderCancelledEvent);
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.orderStatus = event.getOrderStatus();
    }
}
