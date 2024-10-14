package com.example.hospitals;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Hospitals Service API",
				description = "Hospital Service for the Hospital Microservices", version = "1.0",
				contact = @Contact(
						name = "marine",
						email = "marinaejoy1@yandex.ru",
						url = "https://github.com/kenyantea/"
				)
		)
)
@SpringBootApplication
public class HospitalsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalsApplication.class, args);
	}

}
