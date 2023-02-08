package com.hoang2001giang.CommonService.events;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Value
public class OrderCompletedEvent {
    private String orderId;
    private String orderStatus="COMPLETED";
}
