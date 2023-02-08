package com.hoang2001giang.ShipmentService.command.api.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Shipment {
    @Id
    private String shipmentId;
    private String orderId;
    private String shipmentStatus;
}
