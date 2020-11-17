package com.mario.integration.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.mario.order.bo.OrderEvent;
import com.mario.order.bo.OrderValue;

@SpringBootApplication
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = "com.mario.*")
@EmbeddedKafka(topics = {"order-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
"spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class OrderEventsControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;
	
	private Consumer<Integer, String> consumer;
	
	@BeforeEach
	public void setUp() {
		Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
		this.consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
		this.embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	}
	
	@AfterEach
	public void tearDown() {
		consumer.close();
	}

	@Test
	@Timeout(5)
	public void postOrderEvent() throws InterruptedException {
		//given
		OrderValue orderValue = OrderValue.builder().orderId(123).orderName("or-0102").orderDevice("iphone").build();
		
		OrderEvent orderEvent = OrderEvent.builder().orderEventId(null).order(orderValue).build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", MediaType.APPLICATION_JSON.toString());
		HttpEntity<OrderEvent> request = new HttpEntity<>(orderEvent, headers);
		
		//when
		ResponseEntity<OrderEvent> responseEntity = restTemplate.exchange("/v1/orderevent", HttpMethod.POST, request, OrderEvent.class);
		
		//then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		
		ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(this.consumer, "order-events");
		String expectedRecord = "{\"orderEventId\":null,\"orderEventType\":\"NEW\",\"order\":{\"orderId\":123,\"orderName\":\"or-0102\",\"orderDevice\":\"iphone\"}}";
		String value = consumerRecord.value();
		assertEquals(expectedRecord, value);
	}

}
