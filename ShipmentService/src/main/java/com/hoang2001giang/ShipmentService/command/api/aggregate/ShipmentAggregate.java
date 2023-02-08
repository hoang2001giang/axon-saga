package com.hoang2001giang.ShipmentService.command.api.aggregate;

import com.hoang2001giang.CommonService.commands.CancelShipmentCommand;
import com.hoang2001giang.CommonService.commands.ShipOrderCommand;
import com.hoang2001giang.CommonService.events.OrderShippedEvent;
import com.hoang2001giang.CommonService.events.ShipmentCancelledEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class ShipmentAggregate {
    @AggregateIdentifier
    private String shipmentId;
    private String orderId;
    private String shipmentStatus;

    public ShipmentAggregate() {}

    @CommandHandler
    public ShipmentAggregate(ShipOrderCommand command) {
        OrderShippedEvent event = new OrderShippedEvent(
                command.getShipmentId(),
                command.getOrderId()
        );
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderShippedEvent event) {
        this.shipmentId = event.getShipmentId();
        this.orderId = event.getOrderId();
    }

    @CommandHandler
    public void handle(CancelShipmentCommand command) {
        ShipmentCancelledEvent event = new ShipmentCancelledEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ShipmentCancelledEvent event) {
        this.shipmentStatus = event.getShipmentStatus();
    }
}
