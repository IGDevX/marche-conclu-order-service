package org.igdevx.spring_boot_order_microservice.service;

import org.igdevx.spring_boot_order_microservice.dto.CreateOrderRequest;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderResponse;
import org.igdevx.spring_boot_order_microservice.dto.PaymentIntentResponse;
import org.igdevx.spring_boot_order_microservice.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest req);
    PaymentIntentResponse createPaymentIntent(String reference);
    void handlePaymentConfirmation(String reference, Map<String, Object> paymentData);
    void updateStatus(String reference, String newStatus);
    Order getOrder(Long id);
    List<Order> listOrders();
}
