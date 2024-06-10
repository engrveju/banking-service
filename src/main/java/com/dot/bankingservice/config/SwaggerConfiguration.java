package com.dot.bankingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customizeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("BankingService")
                        .version("1")
                        .description("DOT Pre employment assessment")
                );
    }

}
