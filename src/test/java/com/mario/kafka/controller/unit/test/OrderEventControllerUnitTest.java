package com.mario.kafka.controller.unit.test;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mario.kafka.controller.OrderEventsController;
import com.mario.kafka.main.OrderEventsProducerApplication;
import com.mario.kafka.producer.OrderEventProducer;
import com.mario.order.bo.OrderEvent;
import com.mario.order.bo.OrderValue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrderEventsController.class)
@AutoConfigureWebMvc
@ContextConfiguration(classes = { OrderEventsProducerApplication.class })
public class OrderEventControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderEventProducer orderEventProducer;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testPostOrderEvent() throws Exception {
		// given
		OrderValue orderValue = OrderValue.builder().orderId(123).orderName("or-0102").orderDevice("iphone").build();

		OrderEvent orderEvent = OrderEvent.builder().orderEventId(null).order(orderValue).build();

		String json = this.objectMapper.writeValueAsString(orderEvent);
		when(orderEventProducer.sendOrderEvent(isA(OrderEvent.class), Mockito.anyString())).thenReturn(null);

		// expect
		mockMvc.perform(post("/v1/orderevent").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	public void whenOrderValueRequestIsNull_throwsStatus400() throws Exception {
		// given
		OrderEvent orderEvent = OrderEvent.builder().orderEventId(null).order(null).build();

		String json = this.objectMapper.writeValueAsString(orderEvent);
		when(orderEventProducer.sendOrderEvent(isA(OrderEvent.class), Mockito.anyString())).thenReturn(null);

		// expect
		mockMvc.perform(post("/v1/orderevent").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void whenOrderValueObjectIsPassedAsNull_throwsStatus400() throws Exception {
		// given
		OrderEvent orderEvent = OrderEvent.builder().orderEventId(null).order(new OrderValue()).build();

		String json = this.objectMapper.writeValueAsString(orderEvent);
		when(orderEventProducer.sendOrderEvent(isA(OrderEvent.class), Mockito.anyString())).thenReturn(null);

		// expect
		mockMvc.perform(post("/v1/orderevent").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}
}
