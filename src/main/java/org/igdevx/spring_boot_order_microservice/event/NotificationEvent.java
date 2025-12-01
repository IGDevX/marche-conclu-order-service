package org.igdevx.spring_boot_order_microservice.event;

/**
 * Event sent to Kafka notification topic
 * This matches the structure expected by notification-service consumer
 * 
 * @param userId User ID of the producer to notify
 * @param message Notification message
 */
public record NotificationEvent(String userId, String message) {
}
