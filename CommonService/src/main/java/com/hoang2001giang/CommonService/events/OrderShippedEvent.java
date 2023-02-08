package com.hoang2001giang.CommonService.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderShippedEvent {
    private String shipmentId;
    private String orderId;
}
