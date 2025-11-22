package org.igdevx.spring_boot_order_microservice.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId);
    }
    
    public OrderNotFoundException(String field, String value) {
        super("Order not found with " + field + ": " + value);
    }
}
