package org.igdevx.spring_boot_order_microservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.spring_boot_order_microservice.client.AccountClient;
import org.igdevx.spring_boot_order_microservice.client.PaymentClient;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderRequest;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderResponse;
import org.igdevx.spring_boot_order_microservice.dto.PaymentIntentResponse;
import org.igdevx.spring_boot_order_microservice.entity.IdempotencyKey;
import org.igdevx.spring_boot_order_microservice.entity.Order;
import org.igdevx.spring_boot_order_microservice.entity.OrderItem;
import org.igdevx.spring_boot_order_microservice.entity.OrderPayment;
import org.igdevx.spring_boot_order_microservice.entity.OrderStatus;
import org.igdevx.spring_boot_order_microservice.entity.DeliveryMode;
import org.igdevx.spring_boot_order_microservice.exception.AccountServiceException;
import org.igdevx.spring_boot_order_microservice.exception.InvalidOrderStateException;
import org.igdevx.spring_boot_order_microservice.exception.OrderNotFoundException;
import org.igdevx.spring_boot_order_microservice.exception.PaymentServiceException;
import org.igdevx.spring_boot_order_microservice.repository.IdempotencyKeyRepository;
import org.igdevx.spring_boot_order_microservice.repository.OrderPaymentRepository;
import org.igdevx.spring_boot_order_microservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final AccountClient accountClient;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final NotificationProducer notificationProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest req) {
        // Handle idempotency
        if (req.getIdempotencyKey() != null && !req.getIdempotencyKey().isBlank()) {
            var existing = idempotencyKeyRepository.findByKey(req.getIdempotencyKey());
            if (existing.isPresent()) {
                try {
                    return objectMapper.readValue(existing.get().getResponseBody(), CreateOrderResponse.class);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize cached response", e);
                }
            }
        }

        // Build order
        Order order = new Order();
        order.setProducer_keycloak_id(req.getProducer_keycloak_id());
        order.setConsumer_keycloak_id(req.getConsumer_keycloak_id());
        order.setProducerInternalId(req.getProducerInternalId());
        order.setCustomerId(req.getCustomerId());
        order.setStatus(OrderStatus.pending);
        order.setReference(UUID.randomUUID().toString());
        
        // Set delivery mode (default to pickup if not provided)
        if (req.getDeliveryMode() != null && !req.getDeliveryMode().isBlank()) {
            try {
                order.setDeliveryMode(DeliveryMode.valueOf(req.getDeliveryMode().toLowerCase()));
            } catch (IllegalArgumentException e) {
                order.setDeliveryMode(DeliveryMode.pickup);
            }
        } else {
            order.setDeliveryMode(DeliveryMode.pickup);
        }

        // Build order items
        List<OrderItem> items = req.getItems().stream()
                .map(dto -> {
                    OrderItem it = new OrderItem();
                    it.setProductId(dto.getProductId());
                    it.setQuantity(dto.getQuantity());
                    it.setUnitPrice(dto.getUnitPrice());
                    it.setSubtotal(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
                    it.setOrder(order);
                    return it;
                }).collect(Collectors.toList());

        order.setItems(items);

        // Total amount
        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        // Send notification
        try {
            notificationProducer.notifyOrderCreated(
                    saved.getProducer_keycloak_id(),
                    saved.getReference(),
                    saved.getTotalAmount().toString());
        } catch (Exception e) {
            log.warn("Failed to send order notification: {}", e.getMessage());
        }

        CreateOrderResponse response = new CreateOrderResponse();
        response.setId(saved.getId());
        response.setReference(saved.getReference());

        // Store idempotency key
        if (req.getIdempotencyKey() != null && !req.getIdempotencyKey().isBlank()) {
            try {
                IdempotencyKey key = new IdempotencyKey();
                key.setKey(req.getIdempotencyKey());
                key.setResponseBody(objectMapper.writeValueAsString(response));
                idempotencyKeyRepository.save(key);
            } catch (Exception e) {
                log.warn("Failed to store idempotency key: {}", e.getMessage());
            }
        }

        return response;
    }

    @Transactional
    @Override
    public void handlePaymentConfirmation(String reference, Map<String, Object> paymentData) {
        Order order = orderRepository.findByReference(reference)
                .orElseThrow(() -> new OrderNotFoundException("reference", reference));

        String paymentIntentId = (String) paymentData.get("paymentIntentId");
        String status = (String) paymentData.get("status");

        // Create payment record
        OrderPayment payment = new OrderPayment();
        payment.setOrder(order);
        payment.setPaymentIntentId(paymentIntentId);
        payment.setStatus(status);
        payment.setAmount(order.getTotalAmount());
        orderPaymentRepository.save(payment);

        // Update order status
        if ("succeeded".equals(status)) {
            order.setStatus(OrderStatus.paid);
        } else {
            order.setStatus(OrderStatus.unpaid);
        }
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void updateStatus(String reference, OrderStatus newStatus) {
        Order order = orderRepository.findByReference(reference)
                .orElseThrow(() -> new OrderNotFoundException("reference", reference));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Override
    public PaymentIntentResponse createPaymentIntent(String reference) {
        Order order = orderRepository.findByReference(reference)
                .orElseThrow(() -> new OrderNotFoundException("reference", reference));

        // Check producer Stripe account
        try {
            boolean hasAccount = accountClient.hasStripeAccount(order.getProducer_keycloak_id());
            if (!hasAccount)
                throw new InvalidOrderStateException("Producer does not have a Stripe account configured");
        } catch (Exception e) {
            throw new AccountServiceException("Failed to verify producer account", e);
        }

        try {
            return paymentClient.createPaymentIntent(reference, order.getTotalAmount(), "usd");
        } catch (Exception e) {
            throw new PaymentServiceException("Failed to create payment intent", e);
        }
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public List<Order> listOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> listOrdersByProducerInternalId(Long producerInternalId) {
        return orderRepository.findByProducerInternalId(producerInternalId);
    }

    @Override
    public List<Order> listOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional
    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        log.info("Updated order {} status to {}", orderId, newStatus);
        
        return updatedOrder;
    }
}
