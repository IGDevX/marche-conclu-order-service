package org.igdevx.spring_boot_order_microservice.client;

import org.igdevx.spring_boot_order_microservice.dto.PaymentIntentResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class PaymentClient {

    private final WebClient webClient;

    public PaymentClient(WebClient paymentWebClient) { this.webClient = paymentWebClient; }

    public PaymentIntentResponse createPaymentIntent(String reference, BigDecimal amount, String currency) {
        var body = java.util.Map.of("reference", reference, "amount", amount, "currency", currency);
        Mono<PaymentIntentResponse> mono = webClient.post()
                .uri("/payments/intent")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PaymentIntentResponse.class)
                .onErrorResume(ex -> {
                    // fallback to stubbed response on error
                    PaymentIntentResponse r = new PaymentIntentResponse();
                    r.setPaymentIntentId("pi_stub_" + java.util.UUID.randomUUID().toString());
                    r.setStatus("error");
                    return Mono.just(r);
                });
        return mono.block();
    }
}
