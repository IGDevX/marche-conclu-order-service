package org.igdevx.spring_boot_order_microservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("Microservice for managing orders in the marketplace")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IGDevX")
                                .url("https://github.com/IGDevX")));
    }
}
