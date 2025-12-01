package org.igdevx.spring_boot_order_microservice.mapper;

import org.igdevx.spring_boot_order_microservice.dto.OrderItemDetailDto;
import org.igdevx.spring_boot_order_microservice.entity.OrderItem;

import java.math.BigDecimal;

public final class OrderMapper {

    private OrderMapper() {}

    /** Converts OrderItemDto to OrderItem entity */
    public static OrderItem toEntity(OrderItemDetailDto dto) {
        if (dto == null) return null;

        OrderItem entity = new OrderItem();
        entity.setProductId(dto.getProductId());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());

        // Calculate subtotal: unitPrice * quantity
        if (dto.getUnitPrice() != null && dto.getQuantity() != null) {
            entity.setSubtotal(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        } else {
            entity.setSubtotal(BigDecimal.ZERO);
        }

        return entity;
    }

    /** Optionally: convert entity to DTO */
    public static OrderItemDetailDto toDto(OrderItem entity) {
        if (entity == null) return null;

        OrderItemDetailDto dto = new OrderItemDetailDto();
        dto.setProductId(entity.getProductId());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setSubtotal(entity.getSubtotal());

        return dto;
    }
}
