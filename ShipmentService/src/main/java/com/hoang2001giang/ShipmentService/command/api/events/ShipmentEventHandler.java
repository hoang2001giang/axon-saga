package com.hoang2001giang.ShipmentService.command.api.events;

import com.hoang2001giang.CommonService.events.OrderShippedEvent;
import com.hoang2001giang.CommonService.events.ShipmentCancelledEvent;
import com.hoang2001giang.ShipmentService.command.api.data.Shipment;
import com.hoang2001giang.ShipmentService.command.api.data.ShipmentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventHandler {
    private ShipmentRepository shipmentRepository;

    public ShipmentEventHandler(ShipmentRepository repository) {
        this.shipmentRepository = repository;
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        Shipment shipment = Shipment.builder()
                .shipmentId(event.getShipmentId())
                .orderId(event.getOrderId())
                .shipmentStatus("COMPLETED")
                .build();

        shipmentRepository.save(shipment);
    }

    @EventHandler
    public void on(ShipmentCancelledEvent event) {
        Shipment shipment
                = shipmentRepository.findById(event.getShipmentId()).get();
        shipment.setShipmentStatus(event.getShipmentStatus());

        shipmentRepository.save(shipment);
    }
}
