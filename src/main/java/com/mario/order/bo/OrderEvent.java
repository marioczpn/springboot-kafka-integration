package com.mario.order.bo;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderEvent {
	
	private Integer orderEventId;
	private OrderEventType orderEventType;
	
	@NotNull
	@Valid
	private OrderValue order;

}
