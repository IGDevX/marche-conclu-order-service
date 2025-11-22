package org.igdevx.spring_boot_order_microservice.mapper;

import org.igdevx.spring_boot_order_microservice.dto.OrderItemDto;
import org.igdevx.spring_boot_order_microservice.entity.OrderItem;

import java.math.BigDecimal;

public final class OrderMapper {
    private OrderMapper() {}

    public static OrderItem toEntity(OrderItemDto dto) {
        OrderItem e = new OrderItem();
        e.setProductId(dto.getProductId());
        e.setQuantity(dto.getQuantity());
        e.setUnitPrice(dto.getUnitPrice());
        BigDecimal qty = BigDecimal.valueOf(dto.getQuantity());
        e.setSubtotal(dto.getUnitPrice().multiply(qty));
        return e;
    }
}
