package com.hoang2001giang.CommonService.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class OrderShippedEvent {
    private String shipmentId;
    private String paymentId;
    private String orderId;
}
