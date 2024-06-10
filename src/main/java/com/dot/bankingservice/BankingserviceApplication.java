package com.dot.bankingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BankingserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingserviceApplication.class, args);
	}

}
