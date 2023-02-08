package com.hoang2001giang.OrderService.command.api.controller;

import com.hoang2001giang.CommonService.events.OrderCreatedEvent;
import com.hoang2001giang.OrderService.command.api.model.OrderRestModel;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderCommandController {

//    private CommandGateway commandGateway;


//    public OrderCommandController(CommandGateway commandGateway) {
//        this.commandGateway = commandGateway;
//    }

    private EventGateway eventGateway;

    public OrderCommandController(EventGateway eventGateway) {
        this.eventGateway = eventGateway;
    }

    @PostMapping
    public String createOrder(@RequestBody OrderRestModel orderRestModel) {
        String id = UUID.randomUUID().toString();

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(id)
                .addressId(orderRestModel.getAddressId())
                .productId(orderRestModel.getProductId())
                .quantity(orderRestModel.getQuantity())
                .userId(orderRestModel.getUserId())
                .orderStatus("CREATED").build();

        eventGateway.publish(event);

        return "Order Created";
    }
}
