package org.igdevx.spring_boot_order_microservice.repository;

import org.igdevx.spring_boot_order_microservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByReference(String reference);

    List<Order> findByProducerInternalId(Long producerInternalId);

    List<Order> findByCustomerId(Long customerId);
}
