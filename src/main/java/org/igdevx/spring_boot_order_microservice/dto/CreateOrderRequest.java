package org.igdevx.spring_boot_order_microservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "Producer ID is required")
    private Long producerId;
    
    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemDto> items;
    
    private String idempotencyKey;
}
