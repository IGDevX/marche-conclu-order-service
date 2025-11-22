package org.igdevx.spring_boot_order_microservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderDetailDto {
    private Long id;
    private String reference;
    private Long producerId;
    private String status;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private List<OrderItemDetailDto> items;
}
