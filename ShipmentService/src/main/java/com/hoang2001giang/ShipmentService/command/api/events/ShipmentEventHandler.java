package com.hoang2001giang.ShipmentService.command.api.events;

import com.hoang2001giang.CommonService.events.OrderShippedEvent;
import com.hoang2001giang.CommonService.events.PaymentProcessedEvent;
import com.hoang2001giang.CommonService.events.ShipmentCancelledEvent;
import com.hoang2001giang.ShipmentService.command.api.data.Shipment;
import com.hoang2001giang.ShipmentService.command.api.data.ShipmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ShipmentEventHandler {
    private ShipmentRepository shipmentRepository;

    @Autowired
    private EventGateway eventGateway;

    public ShipmentEventHandler(ShipmentRepository repository) {
        this.shipmentRepository = repository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {

        log.info("PaymentProcessedEvent for Order ID: {}", paymentProcessedEvent.getOrderId());
        log.info(String.valueOf(paymentProcessedEvent));


        OrderShippedEvent orderShippedEvent =
                OrderShippedEvent.builder()
                        .orderId(paymentProcessedEvent.getOrderId())
                        .paymentId(paymentProcessedEvent.getPaymentId())
                        .shipmentId(UUID.randomUUID().toString())
                        .build();

        try {
            if (true) throw new Exception();

            eventGateway.publish(orderShippedEvent);

        } catch(Exception e) {
            log.error(e.getMessage());
            // start compensating action
            cancelShipment(orderShippedEvent);
        }
    }

    private void cancelShipment(OrderShippedEvent orderShippedEvent) {
        ShipmentCancelledEvent shipmentCancelledEvent =
                new ShipmentCancelledEvent(
                        orderShippedEvent.getShipmentId(),
                        orderShippedEvent.getPaymentId(),
                        orderShippedEvent.getOrderId()
                );
        eventGateway.publish(shipmentCancelledEvent);
    }

    @EventHandler
    public void on(OrderShippedEvent event) {

        log.info("OrderShippedEvent for Order ID: {}", event.getOrderId());
        log.info(String.valueOf(event));


        Shipment shipment = Shipment.builder()
                .shipmentId(event.getShipmentId())
                .orderId(event.getOrderId())
                .shipmentStatus("COMPLETED")
                .build();

        shipmentRepository.save(shipment);
    }

    @EventHandler
    public void on(ShipmentCancelledEvent event) {
        log.info("ShipmentCancelledEvent for Order ID: {}", event.getOrderId());
        log.info(String.valueOf(event));

        Shipment shipment
                = shipmentRepository.findById(event.getShipmentId()).get();
        shipment.setShipmentStatus(event.getShipmentStatus());

        shipmentRepository.save(shipment);

    }

}
