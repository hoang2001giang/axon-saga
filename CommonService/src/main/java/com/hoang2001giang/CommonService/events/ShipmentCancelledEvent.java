package com.hoang2001giang.CommonService.events;

import lombok.Data;

@Data
public class ShipmentCancelledEvent {
    private String shipmentId;
    private String orderId;
    private String shipmentStatus;
}
