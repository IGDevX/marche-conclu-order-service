package org.igdevx.spring_boot_order_microservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.igdevx.spring_boot_order_microservice.entity.DeliveryMode;
import org.igdevx.spring_boot_order_microservice.entity.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDto {
    private Long id;
    private String reference;
    private String producerKeycloakId;
    private String consumerKeycloakId;
    private Long producerInternalId;
    private Long customerId;
    private OrderStatus status;
    private DeliveryMode deliveryMode;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemDetailDto> items;
}
