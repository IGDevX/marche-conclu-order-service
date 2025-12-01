package org.igdevx.spring_boot_order_microservice.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import org.igdevx.spring_boot_order_microservice.entity.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {
    
    @NotNull
    private OrderStatus status;
}
