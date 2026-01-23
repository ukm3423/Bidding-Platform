package com.bidding.platform.seller.dto;

import lombok.Data;

@Data
public class BidPlaceRequest {
    private Long requirementId; // Which requirement are they bidding on?
    private Double bidAmount;   // The price they are offering
    private String deliveryTime;// e.g. "6-8 days"
    private Integer moq;        // Minimum Order Quantity (e.g. 500)
    private Integer offeredQty; // How much they can supply (e.g. 1200)
}