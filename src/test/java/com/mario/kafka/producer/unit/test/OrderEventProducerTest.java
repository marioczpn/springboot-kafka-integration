package com.mario.kafka.producer.unit.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mario.kafka.producer.OrderEventProducer;
import com.mario.order.bo.OrderEvent;
import com.mario.order.bo.OrderValue;

@ExtendWith(MockitoExtension.class)
public class OrderEventProducerTest {
	@Mock
	private KafkaTemplate<Integer, String> kafkaTemplate;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private OrderEventProducer eventProducer;

	@SuppressWarnings("unchecked")
	@Test
	void testSendOrderEventFailure() throws JsonProcessingException, ExecutionException, InterruptedException {
		// given
		OrderValue orderValue = OrderValue.builder().orderId(123).orderName("or-0102").orderDevice("iphone").build();

		OrderEvent orderEvent = OrderEvent.builder().orderEventId(null).order(orderValue).build();

		// when
		SettableListenableFuture<?> future = new SettableListenableFuture<>();
		future.setException(new RuntimeException("Exception Calling Kafka"));
		when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

		// expect
		assertThrows(Exception.class, () -> eventProducer.sendOrderEvent(orderEvent, "order-events").get());

	}

	@SuppressWarnings("unchecked")
	@Test
	void testSendOrderEventSuccess() throws JsonProcessingException, ExecutionException, InterruptedException {
		// given
		final String topic = "order-events";

		OrderValue orderValue = OrderValue.builder().orderId(123).orderName("or-0102").orderDevice("iphone").build();

		OrderEvent orderEvent = OrderEvent.builder().orderEventId(null).order(orderValue).build();

		String record = objectMapper.writeValueAsString(orderEvent);
		SettableListenableFuture future = new SettableListenableFuture<>();

		ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(topic, orderEvent.getOrderEventId(),
				record);
		RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(topic, 1), 1, 1, 342,
				System.currentTimeMillis(), 1, 2);
		SendResult<Integer, String> sendResult = new SendResult<Integer, String>(producerRecord, recordMetadata);

		future.set(sendResult);
		when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

		// when
		ListenableFuture<SendResult<Integer, String>> listenableFuture = eventProducer.sendOrderEvent(orderEvent,
				topic);

		// then
		SendResult<Integer, String> result = listenableFuture.get();
		assertEquals(1, result.getRecordMetadata().partition());

	}
}
