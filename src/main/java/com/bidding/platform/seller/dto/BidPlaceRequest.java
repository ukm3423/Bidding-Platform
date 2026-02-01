package com.bidding.platform.seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BidPlaceRequest {
	
	@NotNull(message = "Requirement ID is missing")
    private Long requirementId; // Which requirement are they bidding on?
    
	@NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be greater than zero")
	private Double bidAmount;   // The price they are offering
	
    @NotBlank(message = "Delivery time is required") // e.g., "5-7 Days"
    private String deliveryTime;// e.g. "6-8 days"
    
    @NotNull(message = "MOQ is required")
    @Positive(message = "MOQ must be positive")
    private Integer moq;        // Minimum Order Quantity (e.g. 500)
    
    @NotNull(message = "Offered quantity is required")
    @Positive(message = "Offered quantity must be positive")
    private Integer offeredQty; // How much they can supply (e.g. 1200)
    
    
}