package org.igdevx.spring_boot_order_microservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull
    @JsonProperty("producer_keycloak_id")
    private String producer_keycloak_id;

    @NotNull
    @JsonProperty("consumer_keycloak_id")
    private String consumer_keycloak_id;

    @NotNull
    @JsonProperty("producerInternalId")
    private Long producerInternalId;

    @NotNull
    @JsonProperty("customerId")
    private Long customerId;

    @NotNull
    @JsonProperty("items")
    private List<OrderItemRequest> items;

    @JsonProperty("deliveryMode")
    private String deliveryMode;

    @JsonProperty("idempotencyKey")
    private String idempotencyKey;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        @NotNull
        private Long productId;

        @NotNull
        private Integer quantity;

        @NotNull
        private BigDecimal unitPrice;
    }
}
