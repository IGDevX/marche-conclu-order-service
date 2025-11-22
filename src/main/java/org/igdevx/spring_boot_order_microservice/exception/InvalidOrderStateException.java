package org.igdevx.spring_boot_order_microservice.exception;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(String message) {
        super(message);
    }
    
    public InvalidOrderStateException(String currentState, String requiredState) {
        super("Invalid order state. Current: " + currentState + ", Required: " + requiredState);
    }
}
