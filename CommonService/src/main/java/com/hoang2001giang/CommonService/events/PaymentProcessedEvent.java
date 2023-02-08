package com.hoang2001giang.CommonService.events;

import com.hoang2001giang.CommonService.models.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class PaymentProcessedEvent {
    private String paymentId;
    private String orderId;
}
