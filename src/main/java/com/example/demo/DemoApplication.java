package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Importa esto
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Importa esto
import org.springframework.security.crypto.password.PasswordEncoder; // Importa esto

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}