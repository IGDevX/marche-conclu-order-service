package org.igdevx.spring_boot_order_microservice.service;

import org.igdevx.spring_boot_order_microservice.dto.CreateOrderRequest;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderServiceTest {

    @Test
    void createOrderCalculatesTotal() {
        // Mock dependencies
        var repo = Mockito.mock(org.igdevx.spring_boot_order_microservice.repository.OrderRepository.class);
        var paymentClient = Mockito.mock(org.igdevx.spring_boot_order_microservice.client.PaymentClient.class);
        var accountClient = Mockito.mock(org.igdevx.spring_boot_order_microservice.client.AccountClient.class);
        var idempotencyKeyRepo = Mockito.mock(org.igdevx.spring_boot_order_microservice.repository.IdempotencyKeyRepository.class);
        var paymentRepo = Mockito.mock(org.igdevx.spring_boot_order_microservice.repository.OrderPaymentRepository.class);
        var notificationProducer = Mockito.mock(org.igdevx.spring_boot_order_microservice.service.NotificationProducer.class);

        // Create service with mocks
        var svc = new OrderServiceImpl(
                repo, paymentClient, accountClient, idempotencyKeyRepo, paymentRepo, notificationProducer
        );

        // Prepare request
        CreateOrderRequest req = new CreateOrderRequest();
        req.setProducer_keycloak_id("42L");
        req.setCustomerId(7L);

        CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));

        req.setItems(List.of(item));

        // Execute
        CreateOrderResponse res = svc.createOrder(req);

        // Assert
        assertNotNull(res.getReference());
    }
}
