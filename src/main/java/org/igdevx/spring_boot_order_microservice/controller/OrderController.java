package org.igdevx.spring_boot_order_microservice.controller;

import jakarta.validation.Valid;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderRequest;
import org.igdevx.spring_boot_order_microservice.dto.CreateOrderResponse;
import org.igdevx.spring_boot_order_microservice.dto.OrderDetailDto;
import org.igdevx.spring_boot_order_microservice.dto.OrderItemDetailDto;
import org.igdevx.spring_boot_order_microservice.dto.PaymentIntentResponse;
import org.igdevx.spring_boot_order_microservice.entity.Order;
import org.igdevx.spring_boot_order_microservice.entity.OrderItem;
import org.igdevx.spring_boot_order_microservice.exception.OrderNotFoundException;
import org.igdevx.spring_boot_order_microservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
        CreateOrderResponse res = orderService.createOrder(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDto> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        if (order == null) {
            throw new OrderNotFoundException(id);
        }
        return ResponseEntity.ok(toDetailDto(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderDetailDto>> listOrders() {
        List<Order> orders = orderService.listOrders();
        List<OrderDetailDto> dtos = orders.stream().map(this::toDetailDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{reference}/payment-intent")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(@PathVariable String reference) {
        PaymentIntentResponse res = orderService.createPaymentIntent(reference);
        return ResponseEntity.ok(res);
    }

    private OrderDetailDto toDetailDto(Order order) {
        OrderDetailDto dto = new OrderDetailDto();
        dto.setId(order.getId());
        dto.setReference(order.getReference());
        dto.setProducerId(order.getProducerId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(order.getItems().stream().map(this::toItemDetailDto).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDetailDto toItemDetailDto(OrderItem item) {
        OrderItemDetailDto dto = new OrderItemDetailDto();
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
