package org.igdevx.spring_boot_order_microservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentConfirmationRequest {
    @NotBlank(message = "Payment intent ID is required")
    private String paymentIntentId;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String errorMessage;
}
