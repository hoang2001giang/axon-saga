package com.hoang2001giang.CommonService.events;

import com.hoang2001giang.CommonService.models.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProcessedEvent {
    private String paymentId;
    private String orderId;
}
