package com.mario.kafka.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mario.kafka.config.YAMLConfig;
import com.mario.kafka.producer.OrderEventProducer;
import com.mario.order.bo.OrderEvent;
import com.mario.order.bo.OrderEventType;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderEventsController {

	@Autowired
	private OrderEventProducer orderEventProducer;

	@Autowired
	private YAMLConfig yamlConfig;

	@PostMapping("/v1/orderevent")
	public ResponseEntity<OrderEvent> postOrderEvent(@Valid @RequestBody OrderEvent orderEvent)
			throws JsonProcessingException {
		final String methodName = "postOrderEvent";
		log.info(methodName + " begins...");

		// Invoke Kafka producer
		orderEvent.setOrderEventType(OrderEventType.NEW);
		this.orderEventProducer.sendOrderEvent(orderEvent, yamlConfig.getKfktopic());

		log.info(methodName + " end.");
		return ResponseEntity.status(HttpStatus.CREATED).body(orderEvent);
	}

}
