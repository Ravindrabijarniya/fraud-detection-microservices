package com.fraud_engine_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableKafka
public class FraudEngineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudEngineServiceApplication.class, args);
	}

}
