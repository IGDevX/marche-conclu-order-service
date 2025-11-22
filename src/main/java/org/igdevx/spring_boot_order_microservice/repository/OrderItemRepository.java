package org.igdevx.spring_boot_order_microservice.repository;

import org.igdevx.spring_boot_order_microservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

