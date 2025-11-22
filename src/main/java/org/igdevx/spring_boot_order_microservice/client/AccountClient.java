package org.igdevx.spring_boot_order_microservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AccountClient {

    private final WebClient webClient;

    public AccountClient(WebClient accountWebClient) { this.webClient = accountWebClient; }

    /**
     * Check whether producer has a connected Stripe account.
     * Returns true if account exists (200), false otherwise.
     */
    public boolean hasStripeAccount(Long producerId) {
        Mono<Boolean> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/producers/{id}/stripe-account").build(producerId))
                .exchangeToMono(resp -> Mono.just(resp.statusCode().is2xxSuccessful()))
                .onErrorReturn(false);
        return mono.block();
    }
}
