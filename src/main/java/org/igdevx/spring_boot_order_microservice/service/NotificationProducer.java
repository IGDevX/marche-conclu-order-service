package org.igdevx.spring_boot_order_microservice.service;

import org.igdevx.spring_boot_order_microservice.event.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for sending notification events to Kafka
 */
@Service
public class NotificationProducer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Value("${notification.topic}")
    private String notificationTopic;

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send notification event to Kafka
     * 
     * @param userId User ID to notify (producer ID)
     * @param message Notification message
     */
    public void sendNotification(String userId, String message) {
        NotificationEvent event = new NotificationEvent(userId, message);
        
        CompletableFuture<SendResult<String, NotificationEvent>> future = 
            kafkaTemplate.send(notificationTopic, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Notification sent successfully to topic '{}' for user '{}': {}", 
                    notificationTopic, userId, message);
            } else {
                logger.error("Failed to send notification to topic '{}' for user '{}': {}", 
                    notificationTopic, userId, ex.getMessage(), ex);
            }
        });
    }

    /**
     * Send order creation notification to producer
     * 
     * @param producerId Producer ID
     * @param orderReference Order reference
     * @param totalAmount Total amount of the order
     */
    public void notifyOrderCreated(String producerId, String orderReference, String totalAmount) {
        String message = String.format(
            "New order created! Reference: %s, Total: %s. Check your dashboard for details.",
            orderReference, totalAmount
        );
        sendNotification(String.valueOf(producerId), message);
    }
}
