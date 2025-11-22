package org.igdevx.spring_boot_order_microservice.repository;

import org.igdevx.spring_boot_order_microservice.entity.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {
}
