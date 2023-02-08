package com.hoang2001giang.OrderService.command.api.saga;

import com.hoang2001giang.CommonService.commands.*;
import com.hoang2001giang.CommonService.events.*;
import com.hoang2001giang.CommonService.models.CardDetails;
import com.hoang2001giang.CommonService.models.User;
import com.hoang2001giang.CommonService.query.GetUserPaymentDetailsQuery;
import com.hoang2001giang.OrderService.command.api.events.OrderCancelledEvent;
import com.hoang2001giang.OrderService.command.api.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
@Slf4j
public class OrderProcessingSaga {
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;

    private String orderId;
    private String paymentId;
    private String shipmentId;

    public OrderProcessingSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent in Saga for Order Id: {}, user id {}", event.getOrderId(), event.getUserId());

        this.orderId = event.getOrderId();

        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery
                = new GetUserPaymentDetailsQuery(event.getUserId());

        User user = null;

        try {

            user = queryGateway.query(
                    getUserPaymentDetailsQuery,
                    ResponseTypes.instanceOf(User.class)
            ).join();
        } catch (Exception e) {
            log.error(e.getMessage());
            // Start compensating transaction
            cancelOrder();
        }

        String paymentId = UUID.randomUUID().toString();

        ValidatePaymentCommand validatePaymentCommand =
                ValidatePaymentCommand.builder()
                        .cardDetails(user.getCardDetails())
                        .orderId(event.getOrderId())
                        .paymentId(paymentId)
                        .build();

        this.paymentId = paymentId;

        commandGateway.sendAndWait(validatePaymentCommand);
    }

    private void cancelOrder() {
        CancelOrderCommand command = new CancelOrderCommand(this.orderId);

        commandGateway.sendAndWait(command);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent event) {
        log.info("PaymentProcessedEvent in Saga for Order Id: {}", event.getOrderId());

        try {
            String shipmentId = UUID.randomUUID().toString();

            ShipOrderCommand shipOrderCommand = ShipOrderCommand.builder()
                    .orderId(event.getOrderId())
                    .shipmentId(shipmentId)
                    .build();

            this.shipmentId = shipmentId;

            commandGateway.sendAndWait(shipOrderCommand);
        } catch(Exception e) {
            log.error(e.getMessage());
            // start compensating transaction
            cancelPayment();
        }
    }

    private void cancelPayment() {
        CancelPaymentCommand command = new CancelPaymentCommand(
                this.paymentId,
                this.orderId
        );
        commandGateway.sendAndWait(command);

        cancelOrder();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderShippedEvent event) {
        log.info("OrderShippedEvent in Saga for Order Id: {}", event.getOrderId());

        try {
//            if (true) throw new Exception();
            CompleteOrderCommand completeOrderCommand = CompleteOrderCommand.builder()
                    .orderId(event.getOrderId())
                    .orderStatus("APPROVED")
                    .build();

            commandGateway.sendAndWait(completeOrderCommand);
        } catch(Exception e) {
            log.error(e.getMessage());
            //start compensating transaction
            cancelShipment();
        }
    }

    private void cancelShipment() {
        CancelShipmentCommand cancelShipmentCommand =
                new CancelShipmentCommand(
                        this.shipmentId,
                        this.orderId
                );
        commandGateway.sendAndWait(cancelShipmentCommand);

        cancelPayment();
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCompletedEvent event) {
        log.info("OrderCompletedEvent in Saga for Order Id: {}", event.getOrderId());

    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCancelledEvent event) {
        log.info("OrderCancelledEvent in Saga for Order Id: {}", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCancelledEvent event) {
        log.info("PaymentCancelledEvent in Saga for Order Id: {}", event.getOrderId());
        cancelOrder();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ShipmentCancelledEvent event) {
        log.info("ShipmentCancelledEvent in Saga for Order Id: {}", event.getOrderId());
    }
}
