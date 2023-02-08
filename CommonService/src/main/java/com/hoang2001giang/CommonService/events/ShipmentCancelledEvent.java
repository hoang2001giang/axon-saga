package com.hoang2001giang.CommonService.events;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class ShipmentCancelledEvent {
    private String shipmentId;
    private String paymentId;
    private String orderId;
    private String shipmentStatus="CANCELLED";
}
