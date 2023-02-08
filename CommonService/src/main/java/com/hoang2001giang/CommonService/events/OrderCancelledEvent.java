package com.hoang2001giang.CommonService.events;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class OrderCancelledEvent {
    private String orderId;
    private String orderStatus="CANCELLED";
}
