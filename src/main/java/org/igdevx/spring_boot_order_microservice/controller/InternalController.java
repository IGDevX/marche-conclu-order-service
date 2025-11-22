package org.igdevx.spring_boot_order_microservice.controller;

import jakarta.validation.Valid;
import org.igdevx.spring_boot_order_microservice.dto.PaymentConfirmationRequest;
import org.igdevx.spring_boot_order_microservice.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/internal/orders")
public class InternalController {

    private final OrderService orderService;

    @Value("${service.api.token:changeme}")
    private String serviceApiToken;

    public InternalController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{reference}/payment-confirmation")
    public ResponseEntity<Void> paymentConfirmation(
            @PathVariable String reference, 
            @RequestHeader("X-Service-Token") String token,
            @Valid @RequestBody PaymentConfirmationRequest request) {
        if (token == null || !token.equals(serviceApiToken)) {
            return ResponseEntity.status(403).build();
        }
        
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("paymentIntentId", request.getPaymentIntentId());
        paymentData.put("status", request.getStatus());
        paymentData.put("errorMessage", request.getErrorMessage());
        
        orderService.handlePaymentConfirmation(reference, paymentData);
        return ResponseEntity.ok().build();
    }
}
