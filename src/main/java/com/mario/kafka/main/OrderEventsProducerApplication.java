package com.mario.kafka.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.mario.kafka.*"})
public class OrderEventsProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderEventsProducerApplication.class, args);
	}

}
