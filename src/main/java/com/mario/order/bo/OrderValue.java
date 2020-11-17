package com.mario.order.bo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderValue {
	
	@NotNull
	private Integer orderId;
	@NotBlank
	private String orderName;
	@NotBlank
	private String	orderDevice;
	

}
