package com.example.timetable;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Timetable Service API",
				description = "Timetable Service for the Hospital Microservices", version = "1.0",
				contact = @Contact(
						name = "marine",
						email = "marinaejoy1@yandex.ru",
						url = "https://github.com/kenyantea/"
				)
		)
)
@SpringBootApplication
public class TimetableApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimetableApplication.class, args);
	}

}
