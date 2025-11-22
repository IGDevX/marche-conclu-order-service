package org.igdevx.spring_boot_order_microservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDetailDto {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
