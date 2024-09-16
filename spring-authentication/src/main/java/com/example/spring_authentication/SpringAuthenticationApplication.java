package com.example.spring_authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringAuthenticationApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringAuthenticationApplication.class, args);
	}
}
