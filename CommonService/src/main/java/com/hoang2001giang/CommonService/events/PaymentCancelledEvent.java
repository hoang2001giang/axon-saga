package com.hoang2001giang.CommonService.events;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class PaymentCancelledEvent {
    private String paymentId;
    private String orderId;
    private String paymentStatus="CANCELLED";
}
