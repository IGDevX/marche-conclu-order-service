package org.igdevx.spring_boot_order_microservice.repository;

import org.igdevx.spring_boot_order_microservice.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByKey(String key);
}
