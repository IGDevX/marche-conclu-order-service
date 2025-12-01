package org.igdevx.spring_boot_order_microservice.service;

import org.igdevx.spring_boot_order_microservice.entity.Order;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderResponse;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderRequest;
import org.igdevx.spring_boot_order_microservice.dto.PaymentIntentResponse;

import java.util.List;
import java.util.Map;
import org.igdevx.spring_boot_order_microservice.entity.OrderStatus;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest req);

    void handlePaymentConfirmation(String reference, Map<String, Object> paymentData);

    void updateStatus(String reference, OrderStatus newStatus);

    PaymentIntentResponse createPaymentIntent(String reference);

    Order getOrder(Long id);

    List<Order> listOrders();

    List<Order> listOrdersByProducerInternalId(Long producerInternalId);

    List<Order> listOrdersByCustomer(Long customerId);

    Order updateOrderStatus(Long orderId, OrderStatus newStatus);
}
