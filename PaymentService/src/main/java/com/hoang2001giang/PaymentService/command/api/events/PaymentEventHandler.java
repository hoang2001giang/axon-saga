package com.hoang2001giang.PaymentService.command.api.events;

import com.hoang2001giang.CommonService.events.*;
import com.hoang2001giang.CommonService.models.User;
import com.hoang2001giang.CommonService.query.GetUserPaymentDetailsQuery;
import com.hoang2001giang.PaymentService.command.api.data.Payment;
import com.hoang2001giang.PaymentService.command.api.data.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class PaymentEventHandler {
    private PaymentRepository paymentRepository;

    @Autowired
    private EventGateway eventGateway;

    @Autowired
    private QueryGateway queryGateway;


    public PaymentEventHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {

        log.info("OrderCreatedEvent for Order ID: {}", orderCreatedEvent.getOrderId());
        log.info(String.valueOf(orderCreatedEvent));

        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery
                = new GetUserPaymentDetailsQuery(orderCreatedEvent.getUserId());

        User user = null;

        String paymentId = UUID.randomUUID().toString();

        try {
            user = queryGateway.query(
                    getUserPaymentDetailsQuery,
                    ResponseTypes.instanceOf(User.class)
            ).join();

//            if (true) throw new Exception();

//            CompleteOrderCommand command = CompleteOrderCommand.builder()
//                    .orderId(order.getOrderId())
//                    .orderStatus("COMPLETED")
//                    .build();
//            commandGateway.send(command);

            PaymentProcessedEvent paymentProcessedEvent =
                    PaymentProcessedEvent.builder()
                            .paymentId(paymentId)
                            .orderId(orderCreatedEvent.getOrderId())
                            .build();

            eventGateway.publish(paymentProcessedEvent);

        } catch (Exception e) {
            log.error(e.getMessage());
            // start compensating action
            cancelPayment(paymentId, orderCreatedEvent.getOrderId());
        }
    }


    @EventHandler
    public void on(PaymentProcessedEvent event) {
        Payment payment
                = Payment.builder()
                .paymentId(event.getPaymentId())
                .orderId(event.getOrderId())
                .paymentStatus("COMPLETED")
                .timeStamp(new Date())
                .build();

        paymentRepository.save(payment);

        log.info("PaymentProcessedEvent for Order ID: {}", payment.getOrderId());
        log.info(String.valueOf(event));

    }

    @EventHandler
    public void on(PaymentCancelledEvent event) {
        Payment payment
                = paymentRepository.findById(event.getPaymentId()).get();

        payment.setPaymentStatus(event.getPaymentStatus());

        paymentRepository.save(payment);

        log.info("PaymentCancelledEvent for Order ID: {}", payment.getOrderId());
        log.info(String.valueOf(event));

    }

    private void cancelPayment(String paymentId, String orderId) {
        PaymentCancelledEvent event =
                new PaymentCancelledEvent(paymentId, orderId);
        eventGateway.publish(event);
    }

    @EventHandler
    public void on(ShipmentCancelledEvent shipmentCancelledEvent) {
        cancelPayment(shipmentCancelledEvent.getPaymentId(), shipmentCancelledEvent.getOrderId());
    }

}
