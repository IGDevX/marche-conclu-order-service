package org.igdevx.spring_boot_order_microservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "service", "order-service",
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "endpoints", Map.of(
                        "swagger-ui", "/swagger-ui/index.html",
                        "api-docs", "/v3/api-docs",
                        "orders", "/orders",
                        "actuator", "/actuator/health"
                )
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "order-service"
        ));
    }
}
