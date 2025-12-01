package org.igdevx.spring_boot_order_microservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntentResponse {
    private String paymentIntentId;
    private String status;
}
