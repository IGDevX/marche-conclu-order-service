package org.igdevx.spring_boot_order_microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${payment.service.url:http://payment-service:5003}")
    private String paymentServiceUrl;

    @Value("${account.service.url:http://account-service:5001}")
    private String accountServiceUrl;

    @Bean
    public WebClient paymentWebClient() {
        return WebClient.builder()
                .baseUrl(paymentServiceUrl)
                .exchangeStrategies(ExchangeStrategies.builder().build())
                .build();
    }

    @Bean
    public WebClient accountWebClient() {
        return WebClient.builder()
                .baseUrl(accountServiceUrl)
                .exchangeStrategies(ExchangeStrategies.builder().build())
                .build();
    }
}
