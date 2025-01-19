package com.GoGym.content_GoGym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication(scanBasePackages = "com.GoGym")
@EntityScan(basePackages = "com.GoGym.module")
@EnableJpaRepositories(basePackages = "com.GoGym")  // Skanuje repozytoria
@ComponentScan(basePackages = {"com.GoGym", "com.GoGym.security"})

public class ContentGoGymApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentGoGymApplication.class, args);
	}
	@GetMapping
	public String hello(){
		return "Hello World";
	}

}
