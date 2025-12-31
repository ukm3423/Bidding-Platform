package com.bidding.platform.admin.dto;



import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductCreateRequest {
	@JsonProperty("categoryId")
	private Long categoryId;
	private String name;
	private String description;
	
	
	

}
