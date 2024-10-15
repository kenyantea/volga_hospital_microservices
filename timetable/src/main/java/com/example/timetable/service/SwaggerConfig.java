package com.example.timetable.service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Timetable Service API",
                description = "Timetable Service for the Hospital Microservices", version = "1.0",
                contact = @Contact(
                        name = "marine",
                        email = "marinaejoy1@yandex.ru",
                        url = "https://github.com/kenyantea/"
                )
        )
)
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Your API Title")
                        .description("API Description")
                        .version("v1"))
                .components(new Components().addSecuritySchemes("JWT", securityScheme()));
    }

    @Bean
    public SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("JWT")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description("Enter JWT token here");
    }
}
