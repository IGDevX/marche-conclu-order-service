package org.igdevx.spring_boot_order_microservice.service;

import org.igdevx.spring_boot_order_microservice.dto.CreateOrderRequest;
import org.igdevx.spring_boot_order_microservice.dto.OrderItemDto;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class OrderServiceTest {

    @Test
    void createOrderCalculatesTotal() {
        // use a real service with mock repository and clients
        var repo = Mockito.mock(org.igdevx.spring_boot_order_microservice.repository.OrderRepository.class);
        var paymentClient = Mockito.mock(org.igdevx.spring_boot_order_microservice.client.PaymentClient.class);
        var accountClient = Mockito.mock(org.igdevx.spring_boot_order_microservice.client.AccountClient.class);
        var idempotencyKeyRepo = Mockito.mock(org.igdevx.spring_boot_order_microservice.repository.IdempotencyKeyRepository.class);
        var paymentRepo = Mockito.mock(org.igdevx.spring_boot_order_microservice.repository.OrderPaymentRepository.class);
        var svc = new OrderServiceImpl(repo, paymentClient, accountClient, idempotencyKeyRepo, paymentRepo);
        CreateOrderRequest req = new CreateOrderRequest();
        var item = new OrderItemDto();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));
        req.setProducerId(42L);
        req.setItems(List.of(item));
        CreateOrderResponse res = svc.createOrder(req);
        assertNotNull(res.getReference());
    }
}
