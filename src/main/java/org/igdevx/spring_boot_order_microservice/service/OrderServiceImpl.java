package org.igdevx.spring_boot_order_microservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.igdevx.spring_boot_order_microservice.client.AccountClient;
import org.igdevx.spring_boot_order_microservice.client.PaymentClient;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderRequest;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderResponse;
import org.igdevx.spring_boot_order_microservice.dto.PaymentIntentResponse;
import org.igdevx.spring_boot_order_microservice.entity.IdempotencyKey;
import org.igdevx.spring_boot_order_microservice.entity.Order;
import org.igdevx.spring_boot_order_microservice.entity.OrderItem;
import org.igdevx.spring_boot_order_microservice.entity.OrderPayment;
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
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final AccountClient accountClient;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final ObjectMapper objectMapper;

    public OrderServiceImpl(OrderRepository orderRepository, PaymentClient paymentClient, AccountClient accountClient,
                            IdempotencyKeyRepository idempotencyKeyRepository, OrderPaymentRepository orderPaymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
        this.accountClient = accountClient;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.orderPaymentRepository = orderPaymentRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest req) {
        // Check idempotency key if provided
        if (req.getIdempotencyKey() != null && !req.getIdempotencyKey().isBlank()) {
            var existing = idempotencyKeyRepository.findByKey(req.getIdempotencyKey());
            if (existing.isPresent()) {
                // Return cached response
                try {
                    return objectMapper.readValue(existing.get().getResponseBody(), CreateOrderResponse.class);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize cached response", e);
                }
            }
        }
        
        Order order = new Order();
        order.setProducerId(req.getProducerId());
        order.setStatus("CREATED");
        List<OrderItem> items = req.getItems().stream().map(dto -> {
            OrderItem it = new OrderItem();
            it.setProductId(dto.getProductId());
            it.setQuantity(dto.getQuantity());
            it.setUnitPrice(dto.getUnitPrice());
            it.setSubtotal(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
            return it;
        }).collect(Collectors.toList());
        // associate items with order
        items.forEach(it -> it.setOrder(order));
        order.setItems(items);
        BigDecimal total = items.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        order.setReference(UUID.randomUUID().toString());
        Order saved = orderRepository.save(order);
        
        CreateOrderResponse response = new CreateOrderResponse(saved.getReference());
        
        // Store idempotency key if provided
        if (req.getIdempotencyKey() != null && !req.getIdempotencyKey().isBlank()) {
            try {
                IdempotencyKey key = new IdempotencyKey();
                key.setKey(req.getIdempotencyKey());
                key.setResponseBody(objectMapper.writeValueAsString(response));
                idempotencyKeyRepository.save(key);
            } catch (Exception e) {
                // Log but don't fail the request
                System.err.println("Failed to store idempotency key: " + e.getMessage());
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
            order.setStatus("PAID");
        } else {
            order.setStatus("PAYMENT_FAILED");
        }
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void updateStatus(String reference, String newStatus) {
        Order order = orderRepository.findByReference(reference)
                .orElseThrow(() -> new OrderNotFoundException("reference", reference));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Override
    public PaymentIntentResponse createPaymentIntent(String reference) {
        // Find order
        Order order = orderRepository.findByReference(reference)
                .orElseThrow(() -> new OrderNotFoundException("reference", reference));
        
        // check producer has stripe account
        try {
            boolean hasAccount = accountClient.hasStripeAccount(order.getProducerId());
            if (!hasAccount) {
                throw new InvalidOrderStateException("Producer does not have a Stripe account configured");
            }
        } catch (Exception e) {
            throw new AccountServiceException("Failed to verify producer account", e);
        }
        
        // call payment service
        try {
            return paymentClient.createPaymentIntent(reference, order.getTotalAmount(), "usd");
        } catch (Exception e) {
            throw new PaymentServiceException("Failed to create payment intent", e);
        }
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> listOrders() {
        return orderRepository.findAll();
    }
}
