package com.mario.kafka.producer;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mario.order.bo.OrderEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderEventProducer {

	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public ListenableFuture<SendResult<Integer, String>> sendOrderEvent(OrderEvent orderEvent, String topic)
			throws JsonProcessingException {
		Integer key = orderEvent.getOrderEventId();
		String value = objectMapper.writeValueAsString(orderEvent);

		ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);

		ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);

		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, value, ex);
			}

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key, value, result);
			}
		});

		return listenableFuture;

	}

	private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
		List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

		return new ProducerRecord<>(topic, null, key, value, recordHeaders);
	}

	private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
		log.info("Message Sent SuccessFully for the key : {} and the value is {}, the partition is {}", key, value,
				result.getRecordMetadata());

	}

	private void handleFailure(Integer key, String value, Throwable ex) {
		log.error("Error Sending the message and the exception is {}", ex.getMessage());

		try {
			throw ex;
		} catch (Throwable e) {
			log.error("Error in OnFailure: {}", e.getMessage());
		}
	}
}
